package com.example.application.data;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Card {

	@Id
	private String cardName;
	private String manaCost;
	private String type;
	private String imageURL;
	private int numberOfCopies;

	public void addCopy() {
		if (numberOfCopies < 100)
			numberOfCopies++;
	}

	public void subtractCopy() {
		if (numberOfCopies > 0)
			numberOfCopies--;
	}

}
