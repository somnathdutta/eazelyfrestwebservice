package com.mkyong.rest;

public class KitchenDetailsBean implements Comparable<KitchenDetailsBean> {

	private Integer kitchenId;
	
	private Integer cuisineId;
	
	private Integer categoryId;
	
	private Double latitude;
	
	private Double longitude;
	
	private Double lunchStock;

	private Double dinnerStock;
	
	private Double stock;
	
	private Double distance;
	
	private Integer kithcenNearestPriority;
	
	public Integer getKitchenId() {
		return kitchenId;
	}

	public void setKitchenId(Integer kitchenId) {
		this.kitchenId = kitchenId;
	}

	public Integer getCuisineId() {
		return cuisineId;
	}

	public void setCuisineId(Integer cuisineId) {
		this.cuisineId = cuisineId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLunchStock() {
		return lunchStock;
	}

	public void setLunchStock(Double lunchStock) {
		this.lunchStock = lunchStock;
	}

	public Double getDinnerStock() {
		return dinnerStock;
	}

	public void setDinnerStock(Double dinnerStock) {
		this.dinnerStock = dinnerStock;
	}

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(KitchenDetailsBean o) {
		if(this.distance > o.distance)
		return 1;
		else return -1;
	}

	public void setKithcenNearestPriority(Integer kithcenNearestPriority) {
		this.kithcenNearestPriority = kithcenNearestPriority;
	}

	public Integer getKithcenNearestPriority() {
		return kithcenNearestPriority;
	}
}
