package com.mastra;

public class EpisodeData {
	
	private String EpisodeID;
	private String EpisodeNumber;
	private String SeasonNumber;
	private String EpisodeName;
	private String EpisodeOverview;
	private String EpisodePoster;
	
	public EpisodeData() {}	
	
	public String toString(){
		String s = "";
		s += "EpisodeName: " + EpisodeName + "\n";
		s += "EpisodeID: " + EpisodeID + "\n";
		s += "EpisodeNumber: " + EpisodeNumber + "\n";
		s += "SeasonNumber: " + SeasonNumber + "\n";
		s += "EpisodeOverview: " + EpisodeOverview + "\n";
		s += "EpisodePoster: " + EpisodePoster + "\n";
		
		return s;
	}

	public String getEpisodeID() {
		return EpisodeID;
	}

	public void setEpisodeID(String episodeID) {
		EpisodeID = episodeID;
	}

	public String getEpisodeNumber() {
		return EpisodeNumber;
	}

	public void setEpisodeNumber(String episodeNumber) {
		EpisodeNumber = episodeNumber;
	}

	public String getSeasonNumber() {
		return SeasonNumber;
	}

	public void setSeasonNumber(String seasonNumber) {
		SeasonNumber = seasonNumber;
	}

	public String getEpisodeName() {
		return EpisodeName;
	}

	public void setEpisodeName(String episodeName) {
		EpisodeName = episodeName;
	}

	public String getEpisodeOverview() {
		return EpisodeOverview;
	}

	public void setEpisodeOverview(String episodeOverview) {
		EpisodeOverview = episodeOverview;
	}

	public String getEpisodePoster() {
		return EpisodePoster;
	}

	public void setEpisodePoster(String episodePoster) {
		EpisodePoster = episodePoster;
	}
	
	

}
