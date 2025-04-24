package com.example.be12fin5verdosewmthisbe.inventory.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.inventory.model.*;
import com.example.be12fin5verdosewmthisbe.inventory.model.dto.*;
import com.example.be12fin5verdosewmthisbe.inventory.repository.InventoryRepository;
import com.example.be12fin5verdosewmthisbe.inventory.repository.ModifyInventoryRepository;
import com.example.be12fin5verdosewmthisbe.inventory.repository.StoreInventoryRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventoryPurchase;
import com.example.be12fin5verdosewmthisbe.market_management.market.model.InventorySale;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventoryPurchaseRepository;
import com.example.be12fin5verdosewmthisbe.market_management.market.repository.InventorySaleRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Menu;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.model.Recipe;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.MenuRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.menu.repository.RecipeRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.order.model.OrderMenu;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderMenuRepository;
import com.example.be12fin5verdosewmthisbe.order.repository.OrderRepository;
import com.example.be12fin5verdosewmthisbe.store.model.Store;
import com.example.be12fin5verdosewmthisbe.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StoreInventoryRepository storeInventoryRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final InventorySaleRepository inventorySaleRepository;
    private final InventoryPurchaseRepository inventoryPurchaseRepository;
    private final ModifyInventoryRepository modifyInventoryRepository;
    private final StoreRepository storeRepository;
    private final RecipeRepository recipeRepository;
    private final MenuRepository menuRepository;

    public StoreInventory registerInventory(InventoryDetailRequestDto dto, Long storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(()->
                new CustomException(ErrorCode.STORE_NOT_EXIST));

        // 이름 중복 검사
        if (storeInventoryRepository.existsByName(dto.getName())) {
            throw new CustomException(ErrorCode.INVENTORY_DUPLICATE_NAME);
        }

        try {
            StoreInventory newStoreInventory = StoreInventory.builder()
                    .name(dto.getName())
                    .miniquantity(dto.getMiniquantity())
                    .unit(dto.getUnit())
                    .store(store)
                    .quantity(BigDecimal.ZERO)
                    .expiryDate(dto.getExpiryDate())
                    .build();

            return storeInventoryRepository.save(newStoreInventory);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_REGISTER_FAIL);
        }
    }

    public StoreInventory totalInventory(InventoryDetailRequestDto dto) {
        // 이름 중복 검사
        if (storeInventoryRepository.existsByName(dto.getName())) {
            throw new CustomException(ErrorCode.INVENTORY_DUPLICATE_NAME);
        }

        try {
            StoreInventory newStoreInventory = StoreInventory.builder()
                    .name(dto.getName())
                    .miniquantity(dto.getMiniquantity())
                    .unit(dto.getUnit())
                    .quantity(BigDecimal.valueOf(10.2))
                    .expiryDate(dto.getExpiryDate())
                    .build();

            return storeInventoryRepository.save(newStoreInventory);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_REGISTER_FAIL);
        }
    }

    public Inventory DetailInventory(InventoryDto dto) {
        StoreInventory storeInventory = storeInventoryRepository.findById(dto.getStoreInventoryId())
                .orElseThrow(()-> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        Integer unitPrice = new BigDecimal(dto.getTotalPrice()).divide(dto.getQuantity(),2, RoundingMode.CEILING).intValue();
        Timestamp purchaseDate = dto.getPurchaseDate();
        LocalDate expiryDate = purchaseDate.toLocalDateTime().toLocalDate().plusDays(storeInventory.getExpiryDate());
        Inventory newInventory = Inventory.builder()
                .purchaseDate(dto.getPurchaseDate())
                .expiryDate(expiryDate)
                .quantity(dto.getQuantity())
                .unitPrice(unitPrice)
                .storeInventory(storeInventory)
                .build();
        return inventoryRepository.save(newInventory);
    }
    // ID로 기존 재고 조회
    public StoreInventory findById(Long inventoryId) {
        return storeInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    public StoreInventory updateInventory(Long inventoryId, InventoryDetailRequestDto dto) {
        try {
            StoreInventory inventory = storeInventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

            inventory.setName(dto.getName());
            inventory.setMiniquantity(dto.getMiniquantity());
            inventory.setUnit(dto.getUnit());
            inventory.setExpiryDate(dto.getExpiryDate());

            return storeInventoryRepository.save(inventory);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVENTORY_UPDATE_FAIL);
        }
    }



    public void deleteById(Long inventoryId) {
        StoreInventory inventory = storeInventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

        try {
            storeInventoryRepository.delete(inventory);
        } catch (DataIntegrityViolationException e) {
            throw new CustomException(ErrorCode.INVENTORY_DELETE_FAIL);
        }
    }
    public List<StoreInventoryDto.responseDto> getAllStoreInventories(Long storeId) {
        return storeInventoryRepository.findByStore_Id(storeId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private StoreInventoryDto.responseDto toDto(StoreInventory storeInventory) {
        return StoreInventoryDto.responseDto.builder()
                .id(storeInventory.getStoreinventoryId())
                .name(storeInventory.getName())
                .expiryDate(storeInventory.getExpiryDate())
                .miniquantity(storeInventory.getMiniquantity())
                .quantity(storeInventory.getQuantity())
                .unit(storeInventory.getUnit())
                .build();
    }

    @Transactional
    public List<InventoryInfoDto.Response> getInventoryList(Long storeId) {

        List<StoreInventory> inventoryList = storeInventoryRepository.findInventoryListByStore(storeId);
        List<InventoryInfoDto.Response> inventoryResponseList = new ArrayList<>();

        for (StoreInventory inventory : inventoryList) {
            String name = inventory.getName();
            BigDecimal quantity = inventory.getQuantity();
            String unit = inventory.getUnit();
            InventoryInfoDto.Response inventoryResponse = InventoryInfoDto.Response.of(name, quantity, unit);
            inventoryResponseList.add(inventoryResponse);
        }
        return(inventoryResponseList);
    }

    @Transactional
    public List<InventoryChangeDto.Response> getSaleList(Long storeId, InventoryChangeDto.DateRequest dto) {

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());


        List<OrderMenu> saleList = orderMenuRepository.findSaleMenusForInventoryByStoreAndPeriod(storeId, startTimestamp, endTimestamp);
        List<InventoryChangeDto.Response> menuSaleList = new ArrayList<>();

        for (OrderMenu orderMenu : saleList) {
            Timestamp date = orderMenu.getOrder().getCreatedAt();
            List<Recipe> RecipeList = orderMenu.getMenu().getRecipeList();
            String changeReason = orderMenu.getMenu().getName();
            int menuQuantity = orderMenu.getQuantity();
            for(Recipe recipe : RecipeList ) {
                String stockName = recipe.getStoreInventory().getName();
                BigDecimal quantity = recipe.getPrice().multiply(BigDecimal.valueOf(menuQuantity));
                String unit = recipe.getStoreInventory().getUnit();
                InventoryChangeDto.Response menuSale = InventoryChangeDto.Response.of(date, stockName, changeReason, quantity, unit);
                menuSaleList.add(menuSale);
            }
        }
        return(menuSaleList);
    }

    @Transactional
    public List<InventoryChangeDto.Response> getMarketList(Long storeId, InventoryChangeDto.DateRequest dto) {

        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        Timestamp startTimestamp = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(endDate.plusDays(1).atStartOfDay());

        List<InventoryChangeDto.Response> MarketSaleList = new ArrayList<>();
        List<InventorySale> saleList = inventorySaleRepository.findMarketSaleForInventoryByStoreAndPeriod(storeId, startTimestamp, endTimestamp);
        InventoryPurchase.purchaseStatus status = InventoryPurchase.purchaseStatus.end;
        List<InventoryPurchase> purchaseList = inventoryPurchaseRepository.findMarketPurchaseForInventoryByStoreAndPeriod(storeId, startTimestamp, endTimestamp, status);


        for (InventorySale inventorySale : saleList) {
            Timestamp date = inventorySale.getCreatedAt();
            String stockName = inventorySale.getStoreInventory().getName();
            String changeReasonq = "판매";
            BigDecimal quantity = inventorySale.getQuantity().negate();
            String unit = inventorySale.getStoreInventory().getUnit();
            InventoryChangeDto.Response saleResponse = InventoryChangeDto.Response.of(date, stockName, changeReasonq, quantity, unit);
            MarketSaleList.add(saleResponse);
        }
        // 장터에서 판매

        for (InventoryPurchase inventoryPurchase : purchaseList) {
            Timestamp date = inventoryPurchase.getCreatedAt();
            String stockName = inventoryPurchase.getInventorySale().getStoreInventory().getName();
            String changeReasonq = "구매";
            BigDecimal quantity = inventoryPurchase.getQuantity();
            String unit = inventoryPurchase.getInventorySale().getStoreInventory().getUnit();
            InventoryChangeDto.Response purchaseResponse = InventoryChangeDto.Response.of(date, stockName, changeReasonq, quantity, unit);
            MarketSaleList.add(purchaseResponse);
        }
        // 장터에서 구매

        return(MarketSaleList);
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
            String stockName = modifyInventory.getInventory().getStoreInventory().getName();
            String changeReasonq = "수정";
            BigDecimal quantity = modifyInventory.getModifyQuantity();
            String unit = modifyInventory.getInventory().getStoreInventory().getUnit();
            InventoryChangeDto.Response saleResponse = InventoryChangeDto.Response.of(date, stockName, changeReasonq, quantity, unit);
            updateSoloList.add(saleResponse);
        }

        return(updateSoloList);
    }




    @Transactional
    public void consumeInventory(Long storeInventoryId, BigDecimal requestedQuantity) {
        List<Inventory> inventories = inventoryRepository
                .findByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(storeInventoryId);

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
            throw new IllegalArgumentException("재고가 부족하여 요청 수량만큼 차감할 수 없습니다.");
        }
    }
    // 전체를 유통기한 빠른 순으로
    public List<Inventory> getSortedInventoriesByExpiry(Long storeInventoryId) {
        return inventoryRepository.findByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(storeInventoryId);
    }

    // 가장 먼저 써야 하는 재고 1개
    public Inventory getFirstInventoryToUse(Long storeInventoryId) {
        return inventoryRepository.findTopByStoreInventory_StoreinventoryIdOrderByExpiryDateAsc(storeInventoryId)
                .orElseThrow(() -> new RuntimeException("해당 storeInventory에 재고가 없습니다."));
    }



    @Transactional
    public InventoryCallDto.Response getInventoryCall(Long storeId) {

        List<StoreInventory> storeInventoryList = storeInventoryRepository.findAllStoreInventoryByStore(storeId);
        int expiringCount=0; // 만료 임박
        int reorderRequiredCount=0; // 발주 필요
        int receivedTodayCount=0;// 금일 입고
        LocalDate today = LocalDate.now();

        for (StoreInventory storeInventory : storeInventoryList) {
            int expiryDate = Optional.ofNullable(storeInventory.getExpiryDate()).orElse(0);
            if(expiryDate ==0){
                throw new CustomException(ErrorCode.STORE_INVENTORY_EXPIRY_NOT_FOUND);
            }
            List<Inventory> inventoryList = storeInventory.getInventoryList();
            for(Inventory inventory : inventoryList){
                LocalDate purchaseDate = inventory.getPurchaseDate().toLocalDateTime().toLocalDate(); //입고 날짜
                LocalDate eachExpiryDate = inventory.getExpiryDate();
                int daysBetween = (int) ChronoUnit.DAYS.between(today, eachExpiryDate);
                int daysTodayBetween = (int) ChronoUnit.DAYS.between(today, purchaseDate);

                if (daysBetween >= 0 && daysBetween <= expiryDate / 10) {
                    expiringCount++;
                }

                if(daysTodayBetween ==0){
                    receivedTodayCount++;
                }


            }
            // 만료 임박

            Integer InteMinQuantity = storeInventory.getMiniquantity(); // 예시 Integer 값
            BigDecimal minQuantity = BigDecimal.valueOf(Optional.ofNullable(InteMinQuantity).orElse(0));
            BigDecimal quantity = storeInventory.getQuantity();

            if(minQuantity.compareTo(quantity) > 0){
                reorderRequiredCount ++;
            }
        }

        InventoryCallDto.Response response = InventoryCallDto.Response.of(expiringCount, reorderRequiredCount, receivedTodayCount);

        return(response);
    }

    @Transactional
    public Integer getTotalUpdateNumber(Long storeId) {

        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        Timestamp startTimestamp = Timestamp.valueOf(monday.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.now());

        int totalUpdateNumber = 0;

        List<ModifyInventory> modifyInventoryList = modifyInventoryRepository.findUpdateListByStoreAndPeriod(storeId, startTimestamp, endTimestamp);

        for (ModifyInventory modifyInventory : modifyInventoryList) {
                totalUpdateNumber++;
            }


        return(totalUpdateNumber);
    }

//    @Transactional
//    public String getMaximumMarketPurchase(Long storeId) {
//
//        LocalDate today = LocalDate.now();
//        LocalDate monthAgo = today.minusMonths(1); // 한 달 전 날짜
//
//        Timestamp startTimestamp = Timestamp.valueOf(monthAgo.atStartOfDay()); // 한 달 전 00:00
//        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.now());
//
//        Map<String, BigDecimal> marketSale = new HashMap<String, BigDecimal>(); // 장터로 얼마 팔았니
//        Map<String, BigDecimal> menuSale = new HashMap<String, BigDecimal>(); // 메뉴로 얼마 팔았니
//
//        List<StoreInventory> storeInventoryList = storeInventoryRepository.findByStore_Id(storeId);
//        for(StoreInventory storeInventory : storeInventoryList){
//            marketSale.put(storeInventory.getName(), BigDecimal.ZERO);
//            menuSale.put(storeInventory.getName(), BigDecimal.ZERO);
//        }
//
//
//        List<StoreInventory> storeMarketInventoryList = storeInventoryRepository.findAllStoreInventoryByStoreAndPeroid(storeId,startTimestamp, endTimestamp);
//        for (StoreInventory storeInventory : storeMarketInventoryList) {
//            List<InventorySale> inventorySaleList = storeInventory.getInventorySaleList();
//            String inventoryName = storeInventory.getName();
//            for(InventorySale inventorySale : inventorySaleList){
//                BigDecimal currentValue = marketSale.get(inventoryName);
//                BigDecimal newValue = currentValue.add(inventorySale.getQuantity());
//                marketSale.put(inventoryName, newValue);
//            }
//        }
//
//        List<StoreInventory> storeMenuInventoryList = storeInventoryRepository.findAllMenuSaleInventoryByStoreAndPeroid(storeId,startTimestamp, endTimestamp);
//
//        for(StoreInventory storeInventory : storeMenuInventoryList){
//            List<Recipe> recipeList = storeInventory.getRecipeList();
//            String inventoryName = storeInventory.getName();
//            for(Recipe recipe : recipeList){
//                Menu menu = recipe.getMenu();
//                BigDecimal recipeQuantity = recipe.getQuantity(); // 메뉴당 얼마나씀?
//
//                List<OrderMenu> orderMenuList = menu.getOrderMenuList();
//                for(OrderMenu orderMenu : orderMenuList){
//                    int orderQuantity = orderMenu.getQuantity();
//                    BigDecimal currentValue = marketSale.get(inventoryName);
//                    BigDecimal newValue = currentValue.add(recipeQuantity.multiply(BigDecimal.valueOf(orderQuantity)));
//                    menuSale.put(inventoryName, newValue);
//                }
//            }
//        }
//
//        String bestInventory = null;
//        BigDecimal highestRatio = BigDecimal.ZERO;
//
//        for (String name : marketSale.keySet()) {
//            BigDecimal market = marketSale.getOrDefault(name, BigDecimal.ZERO);
//            BigDecimal menu = menuSale.getOrDefault(name, BigDecimal.ZERO);
//
//            if (menu.compareTo(BigDecimal.ZERO) == 0) {
//                continue; // ❗ 메뉴로 사용한 적 없으면 제외 (또는 처리 방식 정의)
//            }
//
//            BigDecimal ratio = market.divide(menu, 4, RoundingMode.HALF_UP); // 소수점 4자리, 반올림
//            if (ratio.compareTo(highestRatio) > 0) {
//                highestRatio = ratio;
//                bestInventory = name;
//            }
//        }
//
//        return bestInventory;
//
//    }

    @Transactional
    public InventoryNotUsed getMaximumMarketPurchase(Long storeId) {

        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusMonths(1);

        Timestamp startTimestamp = Timestamp.valueOf(monthAgo.atStartOfDay());
        Timestamp endTimestamp = Timestamp.valueOf(LocalDateTime.now());

        Map<String, BigDecimal> marketSale = new HashMap<>();
        Map<String, BigDecimal> menuSale = new HashMap<>();
        Map<String, String> menuUnit = new HashMap<>();

        // 1. 모든 재고 이름 초기화
        List<StoreInventory> storeInventoryList = storeInventoryRepository.findByStore_Id(storeId);
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
    }


    public InventoryRecipes.Response getInventoryRecipes(Long storeId, Long inventoryId) {
        StoreInventory storeInventory = storeInventoryRepository.findById(inventoryId).orElseThrow(()->
                new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
        List<Recipe> recipes = recipeRepository.findAllByStoreInventory(storeInventory);
        List<String> list = new ArrayList<>();
        for(Recipe recipe : recipes){
            Menu menu = menuRepository.findById(recipe.getMenu().getId()).orElseThrow(()->
                    new CustomException(ErrorCode.MENU_NOT_FOUND));;
            list.add(menu.getName());
        }
        return InventoryRecipes.Response.from(list);
    }
}