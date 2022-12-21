package com.example.application.data;

import javax.persistence.Entity;

import com.example.application.data.entity.AbstractEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Card extends AbstractEntity {

	private String cardName;
	private String manaCost;
	private String type;
	private String imageURL;

}
