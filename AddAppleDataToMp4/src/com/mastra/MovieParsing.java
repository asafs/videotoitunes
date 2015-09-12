package com.mastra;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class MovieParsing {

	//String srcURL = 
	static final private String scheme = "http";
	static final private String authority = "www.omdbapi.com";
	static private String srcURLPath = "/";

	private MovieData movieData = new MovieData();

	public MovieParsing(){}

	public void parse(String movieName) throws IOException{
		// get movie URL
		String fragment = "";
		fragment += "t=" + movieName;
		fragment += "&r=xml";
		
		String url = null;
		try {
			url = new URI(
					scheme,
					authority,
					srcURLPath,
					fragment,
					null).toString();
		} catch (URISyntaxException e) {
			// TODO generate error
			System.out.println("Bad Movie Path");
			return;
		}
		
		System.out.println(url);
		
		Document doc = DomUtils.getDomFromURL(url);
		DomUtils.echo(doc);
		//Node series = DomUtils.findSubNode("root", doc);

		/*movieData.setSeriesName(DomUtils.getText(DomUtils.findSubNode("SeriesName", series)));
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
		}*/
	}

}
