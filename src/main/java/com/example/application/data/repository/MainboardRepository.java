package com.example.application.data.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.application.data.Card;

public interface MainboardRepository extends JpaRepository<Card, UUID> {

}