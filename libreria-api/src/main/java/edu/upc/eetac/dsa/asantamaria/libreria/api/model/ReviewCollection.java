package edu.upc.eetac.dsa.asantamaria.libreria.api.model;

import java.util.ArrayList;
import java.util.List;

public class ReviewCollection {

	
	private List<Review> list;
	
	public ReviewCollection() {
		super();
		list = new ArrayList<>();
	}

	public List<Review> getReview() {
		return list;
	}

	public void setReview(List<Review> list) {
		this.list = list;
	}

	public void addReview(Review review) {
		list.add(review);
	}
}
