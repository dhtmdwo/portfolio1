package com.example.inventoryservice.inventory.service;

import com.example.common.common.config.CustomException;
import com.example.common.common.config.ErrorCode;
import com.example.common.kafka.dto.InventoryRegisteredEvent;
import com.example.common.kafka.dto.StoreInventoryDeleteEvent;
import com.example.common.kafka.dto.StoreInventoryEvent;
import com.example.common.kafka.dto.StoreInventoryUpdatedEvent;
import com.example.inventoryservice.inventory.model.Inventory;
import com.example.inventoryservice.inventory.model.ModifyInventory;
import com.example.inventoryservice.inventory.model.StoreInventory;
import com.example.inventoryservice.inventory.model.UsedInventory;
import com.example.inventoryservice.inventory.model.dto.*;
import com.example.inventoryservice.inventory.repository.InventoryRepository;
import com.example.inventoryservice.inventory.repository.ModifyInventoryRepository;
import com.example.inventoryservice.inventory.repository.StoreInventoryRepository;
import com.example.inventoryservice.inventory.repository.UsedInventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final ModifyInventoryRepository modifyInventoryRepository;
    private final UsedInventoryRepository usedInventoryRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "store-inventory-events";
    private static final String DELETE_TOPIC = "store-inventory-delete-events";
    private static final String UPDATED_TOPIC = "store-inventory-updated-events";

    private static final String INVENTORY_TOPIC = "inventory-registered-events";

    public StoreInventory registerStoreInventory(InventoryDetailRequestDto dto, Long storeId) {

        // 이름 중복 검사
        if (storeInventoryRepository.existsByStoreIdAndName(storeId, dto.getName())) {
            throw new CustomException(ErrorCode.INVENTORY_DUPLICATE_NAME);
        }

        StoreInventory newStoreInventory = StoreInventory.builder()
                .name(dto.getName())
                .minQuantity(dto.getMinQuantity())
                .unit(dto.getUnit())
                .storeId(storeId)
                .quantity(BigDecimal.ZERO)
                .expiryDate(dto.getExpiryDate())
                .build();

        StoreInventory saved = storeInventoryRepository.save(newStoreInventory);

        StoreInventoryEvent event = new StoreInventoryEvent(
                saved.getId(),
                saved.getName(),
                saved.getQuantity(),
                saved.getMinQuantity(),
                saved.getExpiryDate(),
                saved.getUnit(),
                saved.getStoreId()
        );

        log.info("Store Inventory Event: {}", event);
        kafkaTemplate.send(TOPIC, String.valueOf(saved.getId()), event);

        return saved;

    }

    public void registerInventory(InventoryDto.InventoryRegisterDto dto) {

        // 1. StoreInventory 조회
        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_INVENTORY_NOT_FOUND));

        // 2. unitPrice 계산 (가격 ÷ 수량 → 올림 처리)
        if (dto.getQuantity() == null || dto.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CustomException(ErrorCode.INVALID_QUANTITY);
        }
        BigDecimal pricePerUnit = new BigDecimal(dto.getPrice())
                .divide(dto.getQuantity(), 0, RoundingMode.CEILING); // 소수점 올림

        // 3. Inventory 생성
        Inventory inventory = Inventory.builder()
                .purchaseDate(new Timestamp(System.currentTimeMillis()))
                .expiryDate(LocalDate.now().plusDays(storeInventory.getExpiryDate()))
                .unitPrice(pricePerUnit.intValue())
                .quantity(dto.getQuantity())
                .storeInventory(storeInventory)
                .build();

        Inventory savedInv = inventoryRepository.save(inventory);

        InventoryRegisteredEvent invEvt = new InventoryRegisteredEvent(
                savedInv.getPurchaseDate(),
                savedInv.getStoreInventory().getId(),
                savedInv.getQuantity(),
                savedInv.getUnitPrice(),
                savedInv.getExpiryDate()
        );

        kafkaTemplate.send(INVENTORY_TOPIC,
                savedInv.getId().toString(),
                invEvt
        );

        storeInventory.setQuantity(storeInventory.getQuantity().add(dto.getQuantity()));

        StoreInventory saved = storeInventoryRepository.save(storeInventory);

        StoreInventoryEvent event = new StoreInventoryEvent(
                saved.getId(),
                saved.getName(),
                saved.getQuantity(),
                saved.getMinQuantity(),
                saved.getExpiryDate(),
                saved.getUnit(),
                saved.getStoreId()
        );

        kafkaTemplate.send(TOPIC, String.valueOf(saved.getId()), event);
    }


    public Inventory totalInventory(TotalInventoryDto dto) {
        // StoreInventory 객체 찾기
        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_INVENTORY_NOT_FOUND));
        LocalDate nowDate = LocalDate.now(); // 찐 유통기한
        LocalDate expriyDate;
        Integer expiryDateInt = dto.getExpiryDateInt();
        if (dto.getExpiryDateInt() == -1) {
            expriyDate = nowDate.plusDays(storeInventory.getExpiryDate());
        } // DB에 저장된 유통기한과 같을 때
        else {
            if (expiryDateInt == null) {
                throw new CustomException(ErrorCode.INVENTORY_REGISTER_FAIL);
            } else {
                expriyDate = nowDate.plusDays(expiryDateInt);
            }
        } // DB에 저장된 유통기한과 다를 때

        // DTO를 Inventory 엔티티로 변환
        Inventory inventory = dto.toEntity(storeInventory, dto, expriyDate);

        // Inventory 엔티티 저장
        Inventory savedInventory = inventoryRepository.save(inventory);

        // StoreInventory 객체 업데이트 (입고 후 재고 수량 업데이트)
        storeInventory.setQuantity(storeInventory.getQuantity().add(savedInventory.getQuantity()));
        StoreInventory saved = storeInventoryRepository.save(storeInventory);

        StoreInventoryEvent event = new StoreInventoryEvent(
                saved.getId(),
                saved.getName(),
                saved.getQuantity(),
                saved.getMinQuantity(),
                saved.getExpiryDate(),
                saved.getUnit(),
                saved.getStoreId()
        );

        kafkaTemplate.send(TOPIC, String.valueOf(saved.getId()), event);

        return savedInventory;  // 등록된 Inventory 객체 반환
    }


    public List<InventoryDto> getInventoriesByStoreInventoryId(Long storeInventoryId) {
        List<Inventory> inventories = inventoryRepository.findByStoreInventory_Id(storeInventoryId);

        return inventories.stream()
                .map(inventory -> {
                    return InventoryDto.builder()
                            .id(inventory.getId())
                            .purchaseDate(inventory.getPurchaseDate())
                            .expiryDate(inventory.getExpiryDate())
                            .quantity(inventory.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void updateInventory(InventoryDetailRequestDto dto) {
        try {
            StoreInventory inventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            boolean exists = storeInventoryRepository.existsByNameAndIdNot(dto.getName(), dto.getStoreInventoryId());
            if (exists) {
                throw new CustomException(ErrorCode.INVENTORY_DUPLICATE_NAME);
            }
            inventory.setName(dto.getName());
            inventory.setMinQuantity(dto.getMinQuantity());
            inventory.setUnit(dto.getUnit());
            inventory.setExpiryDate(dto.getExpiryDate());

            StoreInventory saved = storeInventoryRepository.save(inventory);

            StoreInventoryEvent event = new StoreInventoryEvent(
                    saved.getId(),
                    saved.getName(),
                    saved.getQuantity(),
                    saved.getMinQuantity(),
                    saved.getExpiryDate(),
                    saved.getUnit(),
                    saved.getStoreId()
            );

            kafkaTemplate.send(TOPIC, String.valueOf(saved.getId()), event);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_UPDATE_FAIL);
        }
    }


    @Transactional
    public void deleteByIds(List<Long> inventoryIds) {
        try {
            storeInventoryRepository.deleteAllById(inventoryIds);
        } catch (EmptyResultDataAccessException e) {
            throw new CustomException(ErrorCode.STORE_INVENTORY_NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_INVENTORY);
        }
        // 삭제 성공 후 이벤트 발행
        StoreInventoryDeleteEvent deleteEvent = new StoreInventoryDeleteEvent(inventoryIds);
        kafkaTemplate.send(DELETE_TOPIC, deleteEvent);
    }

    public Page<StoreInventoryDto.responseDto> getAllStoreInventories(Long storeId, Pageable pageable, String keyword) {
        return storeInventoryRepository.findByStoreAndNameContainingWithFetch(storeId, keyword, pageable)
                .map(this::toDto);
    }

    private StoreInventoryDto.responseDto toDto(StoreInventory storeInventory) {
        return StoreInventoryDto.responseDto.builder()
                .id(storeInventory.getId())
                .name(storeInventory.getName())
                .expiryDate(storeInventory.getExpiryDate())
                .minQuantity(storeInventory.getMinQuantity())
                .quantity(storeInventory.getQuantity())
                .unit(storeInventory.getUnit())
                .build();
    }

    @Transactional
    public void deleteById(List<Long> inventoryid) {
        // 먼저 주어진 inventoryIds를 기준으로 해당하는 Inventory 목록을 조회
        List<Inventory> inventories = inventoryRepository.findAllById(inventoryid);

        if (inventories.isEmpty()) {
            throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);  // 조회된 재고가 없으면 예외 처리
        }

        try {
            // 해당 재고들을 삭제
            inventoryRepository.deleteAll(inventories);  // 삭제
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_INVENTORY);  // 삭제할 수 없는 경우 예외 처리
        }
    }

    @Transactional
    public List<InventoryListDto> getInventoryList(Long storeId) {
        return storeInventoryRepository.fetchInventoryInfoByStore(storeId);
    }

    @Transactional
    public List<TotalResponseDto.Response> getDetailedTotalInventoryList(Long storeId, Long storeInventoryId) {
        List<Inventory> inventoryList = inventoryRepository.findByStoreInventoryStoreIdANDStoreInAndInventoryId(storeId, storeInventoryId);
        List<TotalResponseDto.Response> responseTotalInventoryList = new ArrayList<>();
        for (Inventory inventory : inventoryList) {
            LocalDate purhcaseDate = inventory.getPurchaseDate().toLocalDateTime().toLocalDate();
            TotalResponseDto.Response response = TotalResponseDto.Response.of(
                    purhcaseDate, inventory.getExpiryDate(),
                    inventory.getQuantity());
            responseTotalInventoryList.add(response);
        }

        return responseTotalInventoryList;
    }

    @Transactional
    public List<InventoryChangeDto.Response> getInventoryChangeList(Long storeId, InventoryChangeDto.DateRequest dto) {
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        List<InventoryChangeDto.Response> inventoryChangeList = new ArrayList<>();
        List<UsedInventory> usedInventoryList = usedInventoryRepository.findUsedInventoryByStoreAndPeriod(storeId, startTimestamp, endTimestamp);

        for (UsedInventory usedInventory : usedInventoryList) {
            Timestamp date = usedInventory.getUsedDate();
            String stockName = usedInventory.getStoreInventory().getName();
            String changeReason;
            Boolean isMenu = usedInventory.getStatus();
            BigDecimal quantity = usedInventory.getTotalquantity();
            String unit = usedInventory.getStoreInventory().getUnit();
            if (isMenu == true) {
                changeReason = "메뉴";
                quantity = quantity.negate();
            } else {
                if (quantity.compareTo(BigDecimal.ZERO) > 0) {
                    changeReason = "장터 구매";
                } else {
                    changeReason = "장터 판매";
                }
            }
            InventoryChangeDto.Response inventoryChange = InventoryChangeDto.Response.of(date, stockName, changeReason, quantity, unit);
            inventoryChangeList.add(inventoryChange);

        }

        return inventoryChangeList;
    }

    @Transactional
    public List<InventoryChangeDto.Response> getUpdateList(Long storeId, InventoryChangeDto.DateRequest dto) {

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        List<InventoryChangeDto.Response> updateSoloList = new ArrayList<>();
        List<ModifyInventory> updateList = modifyInventoryRepository.findUpdateListByStoreAndPeriod(storeId, startTimestamp, endTimestamp);

        for (ModifyInventory modifyInventory : updateList) {
            Timestamp date = modifyInventory.getModifyDate(); // 수정 날짜
            String stockName = modifyInventory.getStoreInventory().getName();
            String changeReasonq = "수정";
            BigDecimal quantity = modifyInventory.getModifyQuantity();
            String unit = modifyInventory.getStoreInventory().getUnit();
            InventoryChangeDto.Response saleResponse = InventoryChangeDto.Response.of(date, stockName, changeReasonq, quantity, unit);
            updateSoloList.add(saleResponse);
        }

        return (updateSoloList);
    }

    @Transactional
    public void consumeInventories(Map<Long, BigDecimal> usedInventoryQty) {
        // 1) 관련된 StoreInventory 엔티티들 한 번에 조회
        List<StoreInventory> storeInventories =
                storeInventoryRepository.findAllById(usedInventoryQty.keySet());
        // 검증: 존재하지 않는 ID가 있으면 에러
        if (storeInventories.size() != usedInventoryQty.size()) {
            throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);
        }

        // 2) 관련된 모든 Inventory(유통기한별 재고) 한 번에 조회 (expiryDate 오름차순 보장)
        List<Inventory> allInventories =
                inventoryRepository.findByStoreInventoryIdInOrderByExpiryDateAsc(
                        new ArrayList<>(usedInventoryQty.keySet())
                );
        // 그룹화: storeInventoryId → List<Inventory>
        Map<Long, List<Inventory>> inventoryGroup = allInventories.stream()
                .collect(Collectors.groupingBy(inv -> inv.getStoreInventory().getId()));

        // 3) 처리 후 일괄 저장/삭제할 목록 준비
        List<StoreInventory> inventoriesToSave = new ArrayList<>();
        List<Inventory> inventoriesToSaveDetail = new ArrayList<>();
        List<Inventory> inventoriesToDelete = new ArrayList<>();
        List<ModifyInventory> modifyInventoriesToSave = new ArrayList<>();

        // 4) 각 StoreInventory별로 소비 로직 실행
        for (StoreInventory si : storeInventories) {
            Long siId = si.getId();
            BigDecimal toConsume = usedInventoryQty.get(siId);
            BigDecimal newTotal = si.getQuantity().subtract(toConsume);
            if (newTotal.compareTo(BigDecimal.ZERO) < 0) {
                modifyInventoriesToSave.add(
                        ModifyInventory.builder()
                                .modifyDate(new Timestamp(System.currentTimeMillis()))
                                .modifyQuantity(newTotal)
                                .modifyRate(BigDecimal.valueOf(100))  // 전량 시도
                                .storeInventory(si)
                                .build()
                );
                // storeinventory 아래있는 inventory 다 삭제
                continue;
            }
            // 남은 총 재고 반영
            si.setQuantity(newTotal);
            inventoriesToSave.add(si);

            // 유통기한별 재고 차감
            BigDecimal remaining = toConsume;
            List<Inventory> group = inventoryGroup.getOrDefault(siId, List.of());
            for (Inventory inv : group) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                BigDecimal avail = inv.getQuantity();
                if (avail.compareTo(remaining) <= 0) {
                    // 이 항목은 전부 소비 → 삭제
                    remaining = remaining.subtract(avail);
                    inventoriesToDelete.add(inv);
                } else {
                    // 일부만 소비 → 수량만 수정
                    inv.setQuantity(avail.subtract(remaining));
                    inventoriesToSaveDetail.add(inv);
                    remaining = BigDecimal.ZERO;
                }
            }
        }

        // 5) 일괄 반영
        storeInventoryRepository.saveAll(inventoriesToSave);
        inventoryRepository.saveAll(inventoriesToSaveDetail);
        inventoryRepository.deleteAll(inventoriesToDelete);
        modifyInventoryRepository.saveAll(modifyInventoriesToSave);

        for (StoreInventory si : inventoriesToSave) {
            StoreInventoryUpdatedEvent evt =
                    new StoreInventoryUpdatedEvent(si.getId(), si.getQuantity());
            kafkaTemplate.send(UPDATED_TOPIC, si.getId().toString(), evt);
        }
    }

    @Transactional
    public void consumeInventory(Long storeInventoryId, BigDecimal requestedQuantity) {
        List<Inventory> inventories = inventoryRepository
                .findByStoreInventory_IdOrderByExpiryDateAsc(storeInventoryId);

        StoreInventory storeInventory = storeInventoryRepository.findById(storeInventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        BigDecimal changeQuantity = storeInventory.getQuantity().subtract(requestedQuantity);

        if (changeQuantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
        }
        storeInventory.setQuantity(changeQuantity);
        storeInventoryRepository.save(storeInventory);

        BigDecimal remaining = requestedQuantity;

        for (Inventory inventory : inventories) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal available = inventory.getQuantity();

            if (available.compareTo(remaining) <= 0) {
                // 전부 쓰고 삭제
                remaining = remaining.subtract(available);
                inventoryRepository.delete(inventory);
            } else {
                // 일부만 사용
                inventory.setQuantity(available.subtract(remaining));
                inventoryRepository.save(inventory);
                remaining = BigDecimal.ZERO;
            }
        }

        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
        }
    }

    // 전체를 유통기한 빠른 순으로
    public List<Inventory> getSortedInventoriesByExpiry(Long storeInventoryId) {
        return inventoryRepository.findByStoreInventory_IdOrderByExpiryDateAsc(storeInventoryId);
    }

    // 가장 먼저 써야 하는 재고 1개
    public Inventory getFirstInventoryToUse(Long storeInventoryId) {
        return inventoryRepository.findTopByStoreInventory_IdOrderByExpiryDateAsc(storeInventoryId)
                .orElse(null);
    }


    @Transactional
    public InventoryCallDto.Response getInventoryCall(Long storeId) {

        List<StoreInventory> storeInventoryList = storeInventoryRepository.findAllWithInventories(storeId);
        int expiringCount = 0; // 만료 임박
        int reorderRequiredCount = 0; // 발주 필요
        int receivedTodayCount = 0;// 금일 입고
        LocalDate today = LocalDate.now();

        for (StoreInventory storeInventory : storeInventoryList) {
            int expiryDate = Optional.ofNullable(storeInventory.getExpiryDate()).orElse(0);
            if (expiryDate == 0) {
                throw new CustomException(ErrorCode.STORE_INVENTORY_EXPIRY_NOT_FOUND);
            }
            List<Inventory> inventoryList = storeInventory.getInventoryList();
            for (Inventory inventory : inventoryList) {
                LocalDate purchaseDate = inventory.getPurchaseDate().toLocalDateTime().toLocalDate(); //입고 날짜
                LocalDate eachExpiryDate = inventory.getExpiryDate();
                int daysBetween = (int) ChronoUnit.DAYS.between(today, eachExpiryDate);
                int daysTodayBetween = (int) ChronoUnit.DAYS.between(today, purchaseDate);

                if (daysBetween >= 0 && daysBetween <= 2) {
                    expiringCount++;
                }
                if (daysTodayBetween == 0) {
                    receivedTodayCount++;
                }
            }
            // 만료 임박
            BigDecimal minQuantity = storeInventory.getMinQuantity();
            BigDecimal quantity = storeInventory.getQuantity();

            if (minQuantity.compareTo(quantity) > 0) {
                reorderRequiredCount++;
            }
        }

        InventoryCallDto.Response response = InventoryCallDto.Response.of(expiringCount, reorderRequiredCount, receivedTodayCount);

        return (response);
    }

    @Transactional
    public InventoryUpdateDto.Response getTotalUpdateNumber(Long storeId) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        Timestamp start = Timestamp.valueOf(firstDayOfMonth.atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDateTime.now());

        // 1) 기간 내 수정 이력 개수
        int totalUpdateNumber = modifyInventoryRepository
                .findUpdateListByStoreAndPeriod(storeId, start, end)
                .size();

        // 2) StoreInventory + Inventory 한 번에 조회 (1쿼리)
        List<StoreInventory> sis = storeInventoryRepository.findAllWithInventories(storeId);

        // 3) 모든 id 수집
        List<Long> storeInventoryIds = sis.stream()
                .map(StoreInventory::getId)
                .toList();

        // 4) ModifyInventory 일괄 조회 (1쿼리)
        List<ModifyInventory> allMods = modifyInventoryRepository.findByStoreInventory_IdIn(storeInventoryIds);

        // 5) storeInventoryId → List<ModifyInventory> 그룹핑
        Map<Long, List<ModifyInventory>> modsByInv = allMods.stream()
                .collect(Collectors.groupingBy(mi -> mi.getStoreInventory().getId()));

        // 6) 집계 로직
        int expiringCount = 0;
        List<InventoryUpdateDto.ItemQuantityDto> highModifyItems = new ArrayList<>();

        for (StoreInventory si : sis) {
            BigDecimal totalStockedQuantity = BigDecimal.ZERO;
            BigDecimal totalModifiedQuantity = BigDecimal.ZERO;

            for (Inventory inv : si.getInventoryList()) {
                Timestamp purchaseTs = inv.getPurchaseDate();
                LocalDate purchaseDate = purchaseTs.toLocalDateTime().toLocalDate();
                LocalDate expiryDate = inv.getExpiryDate();

                if (!purchaseDate.isBefore(start.toLocalDateTime().toLocalDate()) &&
                        !purchaseDate.isAfter(end.toLocalDateTime().toLocalDate())) {
                    totalStockedQuantity = totalStockedQuantity.add(
                            Optional.ofNullable(inv.getQuantity()).orElse(BigDecimal.ZERO)
                    );

                    // 이전에 로드해 둔 맵에서 꺼내기
                    List<ModifyInventory> mods = modsByInv.getOrDefault(si.getId(), List.of());
                    for (ModifyInventory mi : mods) {
                        Timestamp modTs = mi.getModifyDate();
                        if (!modTs.before(start) && !modTs.after(end)) {
                            totalModifiedQuantity = totalModifiedQuantity.add(
                                    Optional.ofNullable(mi.getModifyQuantity())
                                            .map(BigDecimal::abs)
                                            .orElse(BigDecimal.ZERO)
                            );
                        }
                    }
                }
            }

            if (totalStockedQuantity.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal ratio = totalModifiedQuantity
                        .divide(totalStockedQuantity, 4, RoundingMode.HALF_UP);
                if (ratio.compareTo(BigDecimal.valueOf(0.1)) >= 0) {
                    highModifyItems.add(InventoryUpdateDto.ItemQuantityDto.builder()
                            .itemName(si.getName())
                            .totalQuantity(totalModifiedQuantity)
                            .build());
                }
            }
        }

        return InventoryUpdateDto.Response.builder()
                .total(totalUpdateNumber)
                .itemQuantityDtoList(highModifyItems)
                .build();
    }


    /*@Transactional
    public InventoryNotUsed getMaximumMarketPurchase(Long storeId) {

        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusMonths(1);

        Timestamp startTimestamp = Timestamp.valueOf(monthAgo.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, BigDecimal> marketSale = new HashMap<>();
        Map<String, BigDecimal> menuSale = new HashMap<>();
        Map<String, String> menuUnit = new HashMap<>();

        // 1. 모든 재고 이름 초기화
        List<StoreInventory> storeInventoryList = storeInventoryRepository.findByStoreId(storeId);
        for (StoreInventory storeInventory : storeInventoryList) {
            String name = storeInventory.getName();
            marketSale.put(name, BigDecimal.ZERO);
            menuSale.put(name, BigDecimal.ZERO); // 초기화는 미리
            menuUnit.put(name,"");
        }

        // 2. 장터 판매량 계산
        List<StoreInventory> storeMarketInventoryList = storeInventoryRepository
                .findAllStoreInventoryByStoreAndPeroid(storeId, startTimestamp, endTimestamp);

        for (StoreInventory storeInventory : storeMarketInventoryList) {
            String inventoryName = storeInventory.getName();
            for (InventorySale inventorySale : storeInventory.getInventorySaleList()) {
                BigDecimal current = marketSale.get(inventoryName);
                marketSale.put(inventoryName, current.add(inventorySale.getQuantity()));
            }
        }

        // 3. 메뉴 사용량은 JPQL로 계산
        List<InventoryMenuUsageDto> menuUsageList = storeInventoryRepository
                .findAllMenuSaleInventoryByStoreAndPeroid(storeId, startTimestamp, endTimestamp);

        for (InventoryMenuUsageDto dto : menuUsageList) {
            menuSale.put(dto.getInventoryName(), dto.getTotalUsedQuantity());
            menuUnit.put(dto.getInventoryName(), dto.getUnit());
        }

        // 4. 비율 계산
        String bestInventory = null;
        BigDecimal highestRatio = BigDecimal.ZERO;
        BigDecimal highest = BigDecimal.ZERO;
        String unit = "";

        for (String name : marketSale.keySet()) {
            BigDecimal market = marketSale.get(name);
            BigDecimal menu = menuSale.get(name);
            String tempUnit = menuUnit.get(name);

            if (menu.compareTo(BigDecimal.ZERO) == 0) continue;

            BigDecimal ratio = market.divide(menu, 4, RoundingMode.HALF_UP);
            if (ratio.compareTo(highestRatio) > 0) {
                highestRatio = ratio;
                bestInventory = name;
                highest = market;
                unit = tempUnit;
            }
        }

        InventoryNotUsed inventoryNotUsed = InventoryNotUsed.of(bestInventory, highest, unit);
        return inventoryNotUsed;
    }*/


    @Transactional
    public void updateInventory(InventoryDto.InventoryUpdateDto dto) {
        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        LocalDate start = inventory.getPurchaseDate().toLocalDateTime().toLocalDate();
        LocalDate end = dto.getExpiryDate();

        if (end.isBefore(start)) {
            throw new CustomException(ErrorCode.INVALID_EXPIRY_DATE);
        }

        // 변경량 계산
        BigDecimal beforeQuantity = inventory.getQuantity();
        BigDecimal afterQuantity = dto.getQuantity();
        BigDecimal changeQuantity = afterQuantity.subtract(beforeQuantity); // (수정 후 - 수정 전)

        // 기존 재고 수정
        inventory.setExpiryDate(dto.getExpiryDate());
        inventory.setQuantity(afterQuantity);
        inventoryRepository.save(inventory);


        StoreInventory storeInventory = inventory.getStoreInventory();

        BigDecimal totalQuantity = storeInventory.getInventoryList().stream()
                .map(Inventory::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        storeInventory.setQuantity(totalQuantity);
        StoreInventory saved = storeInventoryRepository.save(storeInventory);

        BigDecimal modifyRate;
        if (beforeQuantity.compareTo(BigDecimal.ZERO) == 0) {
            modifyRate = BigDecimal.valueOf(100); // 0 → X 로 변경: 100% 증가로 간주
        } else {
            modifyRate = changeQuantity
                    .divide(beforeQuantity, 4, RoundingMode.HALF_UP)  // 소수점 4자리
                    .multiply(BigDecimal.valueOf(100));                // % 환산
        }

        // 변경 이력 저장
        ModifyInventory modifyInventory = ModifyInventory.builder()
                .modifyDate(new Timestamp(System.currentTimeMillis()))
                .modifyQuantity(changeQuantity)
                .modifyRate(modifyRate)
                .storeInventory(storeInventory)
                .build();

        modifyInventoryRepository.save(modifyInventory);

        StoreInventoryEvent event = new StoreInventoryEvent(
                saved.getId(),
                saved.getName(),
                saved.getQuantity(),
                saved.getMinQuantity(),
                saved.getExpiryDate(),
                saved.getUnit(),
                saved.getStoreId()
        );

        kafkaTemplate.send(TOPIC, String.valueOf(saved.getId()), event);
    }
}