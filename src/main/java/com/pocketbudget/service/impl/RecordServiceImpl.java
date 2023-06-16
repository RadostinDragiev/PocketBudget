package com.pocketbudget.service.impl;

import com.pocketbudget.repository.RecordRepository;
import com.pocketbudget.service.RecordService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RecordServiceImpl(RecordRepository recordRepository, ModelMapper modelMapper) {
        this.recordRepository = recordRepository;
        this.modelMapper = modelMapper;
    }
}
