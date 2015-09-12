package com.mastra;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SeriesParsing {
	static final private String apikey = "F663F221DD88C13A";

	//String srcURL = 
	static final private String scheme = "http";
	static final private String authority = "thetvdb.com";
	static private String srcURLPath = "/api/" + apikey + "/";
	static private String srcPosterPath = "/banners/";

	private String lang = "en";
	private SeriesData seriesData = new SeriesData();
	private List<EpisodeData> episodeList = new ArrayList<EpisodeData>();

	public SeriesParsing(){}

	public void parse(String SeriesName) throws IOException{

		String id = getSeriesID(SeriesName);
		getSeriesData(id);
	}

	public boolean setLanguage(String fullLang) throws IOException{

		// get Language URL
		String url = null;
		try {
			url = new URI(
					scheme,
					authority,
					srcURLPath + "languages.xml",
					null).toString();
		} catch (URISyntaxException e) {
			// TODO generate error
			System.out.println("Bad Languate Path");
			return false;
		}
		Document doc = DomUtils.getDomFromURL(url);

		List<Node> list = DomUtils.findAllSubNodes("Language", doc);

		Iterator<Node> iter = list.iterator();
		Node correctLang = null;
		while (iter.hasNext()){
			Node n = iter.next();
			Node seriesNameNode = DomUtils.findSubNode("name", doc);
			if (DomUtils.getText(seriesNameNode).equals(fullLang)){
				correctLang = n;
				break;
			}
		}
		if (correctLang != null){
			System.out.println("Found language: " + fullLang);
			lang = DomUtils.getText(DomUtils.findSubNode("abbreviation", correctLang));
			return true;
		}
		else{
			System.out.println("No Language type found for " + fullLang);
			return false;
		}

	}

	public EpisodeData getEpisode(int Season, int Episode){
		
		Iterator<EpisodeData> iter = episodeList.iterator();
		while(iter.hasNext()){
			EpisodeData epi = iter.next();
			if (epi.getSeasonNumber().equals(Integer.toString(Season))) {
				if (epi.getEpisodeNumber().equals(Integer.toString(Episode))) {
					return epi;
				}
			}
		}
		
		return null;
	}
	
	private String getSeriesID(String seriesName) throws IOException{

		// get Series URL
		String url = null;
		try {
			url = new URI(
					"http",
					"thetvdb.com",
					"/api/GetSeries.php",
					"seriesname=" + seriesName,
					null).toString();
		} catch (URISyntaxException e) {
			// TODO generate error
			System.out.println("Bad Series Name");
			return null;
		}

		// get the DOM
		Document doc = DomUtils.getDomFromURL(url);

		// find the real series node
		List<Node> list = DomUtils.findAllSubNodes("Series", doc);

		Iterator<Node> iter = list.iterator();
		Node correctSeries = null;
		while (iter.hasNext()){
			Node n = iter.next();
			Node seriesNameNode = DomUtils.findSubNode("SeriesName", doc);
			if (DomUtils.getText(seriesNameNode).equals(seriesName)){
				correctSeries = n;
				break;
			}
		}
		if (correctSeries != null){
			System.out.println("Found series: " + seriesName);
			return DomUtils.getText(DomUtils.findSubNode("seriesid", correctSeries));
		}
		else{
			System.out.println("No series found for name " + seriesName);
		}
		return null;
	}

	private void getSeriesData(String id) throws IOException{
		// get Series URL
		String url = null;
		try {
			url = new URI(
					scheme,
					authority,
					srcURLPath + "series/" + id + "/all/" + lang + ".xml",
					null).toString();
		} catch (URISyntaxException e) {
			// TODO generate error
			System.out.println("Bad Series Path");
			return;
		}
		Document doc = DomUtils.getDomFromURL(url);

		Node series = DomUtils.findSubNode("Series", doc);

		seriesData.setSeriesName(DomUtils.getText(DomUtils.findSubNode("SeriesName", series)));
		seriesData.setIMDB_ID(DomUtils.getText(DomUtils.findSubNode("IMDB_ID", series)));
		seriesData.setSeriesID(DomUtils.getText(DomUtils.findSubNode("id", series)));
		seriesData.setGenre(DomUtils.getText(DomUtils.findSubNode("Genre", series)));
		seriesData.setSeriesOverview(DomUtils.getText(DomUtils.findSubNode("Overview", series)));
		try {
			String poster = DomUtils.getText(DomUtils.findSubNode("poster", series));
			String PosterURL = new URI(
					scheme,
					authority,
					srcPosterPath + poster,
					null).toString();
			seriesData.setPosterURL(PosterURL);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			System.out.println("Problem with poster URL, not adding");
		}
		getEpisodeData(doc);
	}

	/*private void getEpisodeData(String id) throws IOException{
		// get Series URL
		String url = null;
		try {
			url = new URI(
					scheme,
					authority,
					srcURLPath + "series/" + id + "/all/" + lang + ".xml",
					null).toString();
		} catch (URISyntaxException e) {
			// TODO generate error
			System.out.println("Bad Series Path");
			return;
		}
		Document doc = DomUtils.getDomFromURL(url);
		
		getEpisodeData(doc);
	}*/

	private void getEpisodeData(Document doc) throws IOException{

		List<Node> episodes = DomUtils.findAllSubNodes("Episode", doc);
		
		Iterator<Node> iter = episodes.iterator();
		while(iter.hasNext()){
			Node episode = iter.next();
			EpisodeData epi = new EpisodeData();
			epi.setEpisodeName(DomUtils.getText(DomUtils.findSubNode("EpisodeName", episode)));
			epi.setEpisodeID(DomUtils.getText(DomUtils.findSubNode("id", episode)));
			epi.setEpisodeNumber(DomUtils.getText(DomUtils.findSubNode("EpisodeNumber", episode)));
			epi.setSeasonNumber(DomUtils.getText(DomUtils.findSubNode("SeasonNumber", episode)));
			epi.setEpisodeOverview(DomUtils.getText(DomUtils.findSubNode("Overview", episode)));
			try {
				String poster = DomUtils.getText(DomUtils.findSubNode("filename", episode));
				String PosterURL = new URI(
						scheme,
						authority,
						srcPosterPath + poster,
						null).toString();
				epi.setEpisodePoster(PosterURL);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				System.out.println("Problem with poster URL, not adding");
			}

			episodeList.add(epi);
		}
	}

	public SeriesData getSeriesData() {
		return seriesData;
	}

	public void setSeriesData(SeriesData seriesData) {
		this.seriesData = seriesData;
	}

	public List<EpisodeData> getEpisodeList() {
		return episodeList;
	}

	public void setEpisodeList(List<EpisodeData> episodeList) {
		this.episodeList = episodeList;
	}
	
	

}
