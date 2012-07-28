package org.cakephp.netbeans.commands;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author junichi11
 */
public class CakePhpCommandXmlParser {

    private List<CakeCommandItem> commands;

    public CakePhpCommandXmlParser(List<CakeCommandItem> commands) {
        this.commands = commands;
    }

    public static void parse(File file, List<CakeCommandItem> commands) throws SAXException {
        CakePhpCommandXmlParser parser = new CakePhpCommandXmlParser(commands);
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(file);
            Element root = document.getDocumentElement();
            if (root.getNodeName().equals("shells")) { // NOI18N
                parser.parseCommandList(root);
            } else if (root.getNodeName().equals("shell")) { // NOI18N
                parser.parseCommand(root);
            } else {
                return;
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ParserConfigurationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected void parseCommandList(Element root) {
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            NamedNodeMap attr = node.getAttributes();
            commands.add(new CakeCommandItem(
                attr.getNamedItem("call_as").getNodeValue(), attr.getNamedItem("provider").getNodeValue(), attr.getNamedItem("name").getNodeValue())); // NOI18N
        }
    }

    protected void parseCommand(Element root) {
        String command = root.getElementsByTagName("command").item(0).getTextContent(); // NOI18N
        String description = root.getElementsByTagName("description").item(0).getTextContent(); // NOI18N
        CakeCommandItem item = new CakeCommandItem(command, description, command);
        NodeList subcommands = root.getElementsByTagName("subcommands"); // NOI18N
        for (int i = 0; i < subcommands.getLength(); i++) {
            Node node = subcommands.item(i);
            NodeList children = node.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                NamedNodeMap attr = child.getAttributes();
                item.addSubcommand(new CakeCommandItem(
                    attr.getNamedItem("name").getTextContent(), // NOI18N
                    attr.getNamedItem("help").getTextContent(), // NOI18N
                    attr.getNamedItem("name").getTextContent()));
            }
        }
        Collections.singletonList(commands.add(item));
    }
}
