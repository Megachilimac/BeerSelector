package com.chilimac.exitsix.beerselector;

public class Beer {

	public int getId() {
		return _id;
	}
	public String getName() {
		return _name;
	}
	public String getBrewer() {
		return _brewer;
	}
	public String getIBU() {
		return _ibu;
	}
	public String getABV() {
		return _abv;
	}
	public String getRating() {
		return _rating;
	}
	public Beer(int _id, String _name, String _brewer, String _ibu,
			String _abv, String _rating) {
		super();
		this._id = _id;
		this._name = _name;
		this._brewer = _brewer;
		this._ibu = _ibu;
		this._abv = _abv;
		this._rating = _rating;
	}
	
	public Beer(String _name, String _brewer, String _ibu,
			String _abv, String _rating) {
		super();
		this._name = _name;
		this._brewer = _brewer;
		this._ibu = _ibu;
		this._abv = _abv;
		this._rating = _rating;
	}
	private int _id;
	private String _name;
	private String _brewer;
	private String _ibu;
	private String _abv;
	private String _rating;
	
	
}
