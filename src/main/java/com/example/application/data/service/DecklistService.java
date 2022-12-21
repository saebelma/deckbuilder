package com.example.application.data.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.application.data.Card;
import com.example.application.data.repository.MainboardRepository;
import com.example.application.data.repository.SideboardRepository;

@Service
public class DecklistService {

	private final MainboardRepository mainboardRepository;
	private final SideboardRepository sideboardRepository;

	public DecklistService(MainboardRepository mainboardRepository, SideboardRepository sideboardRepository) {
		this.mainboardRepository = mainboardRepository;
		this.sideboardRepository = sideboardRepository;
	}

	public List<Card> loadMainboard() {
		return mainboardRepository.findAll();
	}

	public List<Card> loadSideboard() {
		return sideboardRepository.findAll();
	}

	public void saveMainboard(List<Card> mainboard) {
		mainboardRepository.deleteAll();
		mainboardRepository.saveAll(mainboard);

	}

	public void saveSideboard(List<Card> sideboard) {
		sideboardRepository.deleteAll();
		sideboardRepository.saveAll(sideboard);

	}
}
