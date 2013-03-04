package HandIn4;

import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class XpathTester {
	private static NodeList nodes;
	private String xmlFile;
	private String xpathExpression;
	private Document doc;
	private XPathExpression expr;
	
	public XpathTester(String xmlFile, String xpathExpression) {
		this.xmlFile = xmlFile;
		this.xpathExpression = xpathExpression;
	}
	
	private void setFile() throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		doc = builder.parse(xmlFile);
	}
	
	private void setCommand() throws XPathExpressionException{
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		expr = xpath.compile(xpathExpression);
	}

	private NodeList getResult() throws XPathExpressionException{
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		return (NodeList) result;
	}	
		
	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
		XpathTester test = new XpathTester("test.xml","(//xsi:KDV)[1]/text()");
		test.setFile();
		test.setCommand();
				
		for (int i = 0; i < test.getResult().getLength(); i++) {
    	System.out.println(test.getResult().item(i).getNodeValue()); 
		}
	}
}