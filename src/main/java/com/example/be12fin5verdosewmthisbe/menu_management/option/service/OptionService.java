package com.example.be12fin5verdosewmthisbe.menu_management.option.service;

import com.example.be12fin5verdosewmthisbe.menu_management.option.model.Option;
import com.example.be12fin5verdosewmthisbe.menu_management.option.model.OptionValue;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionRepository;
import com.example.be12fin5verdosewmthisbe.menu_management.option.repository.OptionValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;

    public Option register(Option option) {
        return optionRepository.save(option);
    }
    public void registerOptionValues(List<OptionValue> optionValues) {
        optionValueRepository.saveAll(optionValues);
    }
}
