package com.example.orderservice.menu_management.option.service;

import com.example.common.CustomException;
import com.example.common.ErrorCode;
import com.example.orderservice.menu_management.option.model.Option;
import com.example.orderservice.menu_management.option.model.OptionValue;
import com.example.orderservice.menu_management.option.model.dto.OptionDto;
import com.example.orderservice.menu_management.option.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;

    @Transactional
    public void registerOption(OptionDto.RegisterRequestDto request,Long storeId) {

        // 중복 체크
        Optional<Option> duplicate = optionRepository.findByStoreIdAndName(storeId,request.getName());
        if(duplicate.isPresent()) {
            throw new CustomException(ErrorCode.OPTION_ALREADY_EXIST);
        }
        Set<Long> uniqueStoreInventoryIds = new HashSet<>();
        for (OptionDto.InventoryQuantityDto ingredient : request.getInventoryQuantities()) {
            if (!uniqueStoreInventoryIds.add(ingredient.getInventoryId())) {
                throw new CustomException(ErrorCode.DUPLICATE_INGREDIENT_IN_RECIPE);
            }
        }

        Option option = Option.builder()
                .name(request.getName())
                .storeId(storeId)
                .price(request.getPrice())
                .build();

        List<OptionValue> optionValues = request.getInventoryQuantities().stream()
                .map(iq -> {

                    return OptionValue.builder()
                            .option(option)
                            .storeInventoryId(iq.getInventoryId())
                            .quantity(iq.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        option.setOptionValueList(optionValues); // 양방향 설정
        optionRepository.save(option);
    }

    @Transactional
    public void updateOption(OptionDto.UpdateRequestDto request, Long storeId) {

        Option option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("옵션을 찾을 수 없습니다. ID: " + request.getOptionId()));

        // 중복 체크
        Optional<Option> duplicate = optionRepository.findByStoreIdAndName(storeId, request.getName());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(request.getOptionId())) {
            throw new CustomException(ErrorCode.OPTION_ALREADY_EXIST);
        }
        Set<Long> uniqueStoreInventoryIds = new HashSet<>();
        for (OptionDto.InventoryQuantityDto ingredient : request.getInventoryQuantities()) {
            if (!uniqueStoreInventoryIds.add(ingredient.getInventoryId())) {
                throw new CustomException(ErrorCode.DUPLICATE_INGREDIENT_IN_RECIPE);
            }
        }

        // 이름, 가격 업데이트
        option.setName(request.getName());
        option.setPrice(request.getPrice());

        // 기존 OptionValue 초기화
        option.getOptionValueList().clear();

        // 새 OptionValue 리스트 생성
        List<OptionValue> newValues = request.getInventoryQuantities().stream()
                .map(iq -> {

                    return OptionValue.builder()
                            .option(option)
                            .storeInventoryId(iq.getInventoryId())
                            .quantity(iq.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        option.getOptionValueList().addAll(newValues); // 양방향 설정
    }



    public void deleteOptions(List<Long> optionIds) {
        List<Option> options = optionRepository.findAllById(optionIds);

        // 존재하지 않는 ID가 있을 경우 예외 처리 (선택사항)
        if (options.size() != optionIds.size()) {
            throw new CustomException(ErrorCode.OPTION_NOT_FOUND);
        }

        optionRepository.deleteAll(options);
    }

    public Page<Option> findAllOptions(Pageable pageable,Long storeId) {
        return optionRepository.findByStoreId(storeId,pageable);
    }

    @Transactional
    public Option findOptionWithValuesById(Long optionId) {
        return optionRepository.findByIdWithOptionValues(optionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다. ID: " + optionId));
    }
    public Page<Option> searchOptionsByKeyword(String keyword, Pageable pageable, Long storeId) {

        return optionRepository.findByStoreIdAndNameContaining(storeId,keyword, pageable);
    }

}