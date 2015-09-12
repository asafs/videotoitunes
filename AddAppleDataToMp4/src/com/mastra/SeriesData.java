package com.mastra;

public class SeriesData {
	
	private String SeriesName;
	private String SeriesID;
	private String Genre;
	private String IMDB_ID;
	private String SeriesOverview;
	private String PosterURL;
	
	public SeriesData(){}
	
	public String toString(){
		String s = "";
		s += "SeriesName: " + SeriesName + "\n";
		s += "SeriesID: " + SeriesID + "\n";
		s += "Genre: " + Genre + "\n";
		s += "IMDB_ID: " + IMDB_ID + "\n";
		s += "SeriesOverview: " + SeriesOverview + "\n";
		s += "PosterURL: " + PosterURL + "\n";
		
		return s;
	}

	public String getSeriesName() {
		return SeriesName;
	}

	public void setSeriesName(String seriesName) {
		SeriesName = seriesName;
	}

	public String getSeriesID() {
		return SeriesID;
	}

	public void setSeriesID(String seriesID) {
		SeriesID = seriesID;
	}

	public String getGenre() {
		return Genre;
	}

	public void setGenre(String genre) {
		Genre = genre;
	}

	public String getIMDB_ID() {
		return IMDB_ID;
	}

	public void setIMDB_ID(String iMDB_ID) {
		IMDB_ID = iMDB_ID;
	}

	public String getSeriesOverview() {
		return SeriesOverview;
	}

	public void setSeriesOverview(String seriesOverview) {
		SeriesOverview = seriesOverview;
	}

	public String getPosterURL() {
		return PosterURL;
	}

	public void setPosterURL(String posterURL) {
		PosterURL = posterURL;
	}
	
	

}
