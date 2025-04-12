package com.example.be12fin5verdosewmthisbe.menu_management.option.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.menu_management.category.model.Category;
import com.example.be12fin5verdosewmthisbe.menu_management.category.repository.CategoryRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.dto.OptionUpdateDto;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OptionService {

    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;
    private final CategoryRepository categoryRepository;

    public Option register(Option option) {
        return optionRepository.save(option);
    }

    public void registerOptionValues(List<OptionValue> optionValues) {
        optionValueRepository.saveAll(optionValues);
    }

    public Option findById(Long optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));
    }

    public Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public List<OptionValue> findOptionValuesByOptionId(Long optionId) {
        return optionValueRepository.findByOptionId(optionId);
    }

    public void updateOption(Option option) {
        optionRepository.save(option);
    }

    public void updateOptionValues(List<OptionValue> optionValues) {
        optionValueRepository.saveAll(optionValues);
    }

    public void deleteOptionValuesByOptionIdAndInventoryIdIn(Long optionId, List<Long> inventoryIdsToDelete) {
        optionValueRepository.deleteByOptionIdAndInventoryIdIn(optionId, inventoryIdsToDelete);
    }

    public void updateOptionWithValues(Long optionId, OptionUpdateDto.RequestDto updateDto) {
        Option existingOption = optionRepository.findById(optionId)
                .orElseThrow(() -> new CustomException(ErrorCode.OPTION_NOT_FOUND));

        if (updateDto.getName() != null) {
            existingOption.setName(updateDto.getName());
        }
        if (updateDto.getPrice() != null) {
            existingOption.setPrice(updateDto.getPrice());
        }
        if (updateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDto.getCategoryId())
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            existingOption.setCategory(category);
        }
        optionRepository.save(existingOption);

        if (updateDto.getInventoryQuantities() != null && !updateDto.getInventoryQuantities().isEmpty()) {
            Map<Long, OptionValue> existingOptionValueMap = findOptionValuesByOptionId(optionId).stream()
                    .collect(Collectors.toMap(OptionValue::getInventoryId, ov -> ov));

            List<OptionValue> toSave = updateDto.getInventoryQuantities().stream()
                    .map(dto -> {
                        OptionValue existingValue = existingOptionValueMap.get(dto.getInventoryId());
                        if (existingValue != null) {
                            existingValue.setQuantity(dto.getQuantity());
                            return existingValue;
                        } else {
                            return OptionValue.builder()
                                    .option(existingOption)
                                    .inventoryId(dto.getInventoryId())
                                    .quantity(dto.getQuantity())
                                    .build();
                        }
                    })
                    .collect(Collectors.toList());

            optionValueRepository.saveAll(toSave);

            List<Long> updatedInventoryIds = updateDto.getInventoryQuantities().stream()
                    .map(OptionUpdateDto.InventoryQuantityUpdateDto::getInventoryId)
                    .collect(Collectors.toList());

            List<Long> inventoryIdsToDelete = existingOptionValueMap.keySet().stream()
                    .filter(inventoryId -> !updatedInventoryIds.contains(inventoryId))
                    .collect(Collectors.toList());

            if (!inventoryIdsToDelete.isEmpty()) {
                optionValueRepository.deleteByOptionIdAndInventoryIdIn(optionId, inventoryIdsToDelete);
            }
        }
    }
}