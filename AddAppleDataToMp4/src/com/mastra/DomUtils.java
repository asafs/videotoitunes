package com.mastra;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DomUtils {
	
	private static int indent = 0;
	private static final String basicIndent = " ";
	
	public static Document getDomFromURL(String url) throws IOException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new URL(url).openStream());
			return doc;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
 	public static List<Node> findAllSubNodes(String name, Node node) {
		
		List<Node> nodeList = new ArrayList<Node>();
		return findAllSubNodesHelper(name, node, nodeList);
	}
	
	public static List<Node> findAllSubNodesHelper(String name, Node node, List<Node> returnList){
	    if (! node.hasChildNodes()) return returnList;

	    NodeList list = node.getChildNodes();
	    for (int i=0; i < list.getLength(); i++) {
	        Node subnode = list.item(i);
	        if (subnode.getNodeType() == Node.ELEMENT_NODE) {
	           if (subnode.getNodeName().equals(name)){ 
	               returnList.add(subnode);
	           }
	        }
	        returnList = findAllSubNodesHelper(name, subnode, returnList);
	    }
	    return returnList;
	}
	
	public static Node findSubNode(String name, Node node) {

	    if (! node.hasChildNodes()) return null;

	    NodeList list = node.getChildNodes();
	    for (int i=0; i < list.getLength(); i++) {
	        Node subnode = list.item(i);
	        if (subnode.getNodeType() == Node.ELEMENT_NODE) {
	           if (subnode.getNodeName().equals(name)) 
	               return subnode;
	        }
	        Node n = findSubNode(name, subnode);
	        if (n != null)
	        	return n;
	    }
	    return null;
	}
	
	public static String getText(Node node) {
	    StringBuffer result = new StringBuffer();
	    if (! node.hasChildNodes()) return "";

	    NodeList list = node.getChildNodes();
	    for (int i=0; i < list.getLength(); i++) {
	        Node subnode = list.item(i);
	        if (subnode.getNodeType() == Node.TEXT_NODE) {
	            result.append(subnode.getNodeValue());
	        }
	        else if (subnode.getNodeType() == Node.CDATA_SECTION_NODE) {
	            result.append(subnode.getNodeValue());
	        }
	        else if (subnode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
	            // Recurse into the subtree for text
	            // (and ignore comments)
	            result.append(getText(subnode));
	        }
	    }

	    return result.toString();
	}

	public static void echo(Node n) {
	    outputIndentation();
	    int type = n.getNodeType();

	    switch (type) {
	        case Node.ATTRIBUTE_NODE:
	            System.out.print("ATTR:");
	            printlnCommon(n);
	            break;

	        case Node.CDATA_SECTION_NODE:
	            System.out.print("CDATA:");
	            printlnCommon(n);
	            break;

	        case Node.COMMENT_NODE:
	            System.out.print("COMM:");
	            printlnCommon(n);
	            break;

	        case Node.DOCUMENT_FRAGMENT_NODE:
	            System.out.print("DOC_FRAG:");
	            printlnCommon(n);
	            break;

	        case Node.DOCUMENT_NODE:
	            System.out.print("DOC:");
	            printlnCommon(n);
	            break;

	        case Node.DOCUMENT_TYPE_NODE:
	            System.out.print("DOC_TYPE:");
	            printlnCommon(n);
	            NamedNodeMap nodeMap = ((DocumentType)n).getEntities();
	            indent += 2;
	            for (int i = 0; i < nodeMap.getLength(); i++) {
	                Entity entity = (Entity)nodeMap.item(i);
	                echo(entity);
	            }
	            indent -= 2;
	            break;

	        case Node.ELEMENT_NODE:
	            System.out.print("ELEM:");
	            printlnCommon(n);

	            NamedNodeMap atts = n.getAttributes();
	            indent += 2;
	            for (int i = 0; i < atts.getLength(); i++) {
	                Node att = (Node) atts.item(i);
	                echo(att);
	            }
	            indent -= 2;
	            break;

	        case Node.ENTITY_NODE:
	            System.out.print("ENT:");
	            printlnCommon(n);
	            break;

	        case Node.ENTITY_REFERENCE_NODE:
	            System.out.print("ENT_REF:");
	            printlnCommon(n);
	            break;

	        case Node.NOTATION_NODE:
	            System.out.print("NOTATION:");
	            printlnCommon(n);
	            break;

	        case Node.PROCESSING_INSTRUCTION_NODE:
	            System.out.print("PROC_INST:");
	            printlnCommon(n);
	            break;

	        case Node.TEXT_NODE:
	            System.out.print("TEXT:");
	            printlnCommon(n);
	            break;

	        default:
	            System.out.print("UNSUPPORTED NODE: " + type);
	            printlnCommon(n);
	            break;
	    }

	    indent++;
	    for (Node child = n.getFirstChild(); child != null;
	         child = child.getNextSibling()) {
	        echo(child);
	    }
	    indent--;
	}
	private static void printlnCommon(Node n) {
	    System.out.print(" nodeName=\"" + n.getNodeName() + "\"");

	    String val = n.getNamespaceURI();
	    if (val != null) {
	        System.out.print(" uri=\"" + val + "\"");
	    }

	    val = n.getPrefix();

	    if (val != null) {
	        System.out.print(" pre=\"" + val + "\"");
	    }

	    val = n.getLocalName();
	    if (val != null) {
	        System.out.print(" local=\"" + val + "\"");
	    }

	    val = n.getNodeValue();
	    if (val != null) {
	        System.out.print(" nodeValue=");
	        if (val.trim().equals("")) {
	            // Whitespace
	            System.out.print("[WS]");
	        }
	        else {
	            System.out.print("\"" + n.getNodeValue() + "\"");
	        }
	    }
	    System.out.println();
	}
	private static void outputIndentation() {
	    for (int i = 0; i < indent; i++) {
	        System.out.print(basicIndent);
	    }
	}
}
