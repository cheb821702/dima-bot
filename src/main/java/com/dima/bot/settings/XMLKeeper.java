package com.dima.bot.settings;

import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.util.URLCheckUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: CHEB
 */
public class XMLKeeper implements SettingsKeeper {

    Logger logger = Logger.getLogger(XMLKeeper.class);

    private static String FILE_PATH = "";
    private static String FILE_NAME = "init.xml";

    private static final String ROOT_ELEMENT = "settings";
    private static final String ACT = "autoCompleteTemplates";
    private static final String URL_WORKERS = "workers";
    private static final String URL_WORKER = "worker";

    private static final String URL_WORKER_URL_PROP = "url";
    private static final String URL_WORKER_MIN_COST_PROP = "minCost";
    private static final String URL_WORKER_MAX_COST_PROP = "maxCost";
    private static final String URL_WORKER_PERCENT_PROP = "percent";
    private static final String URL_WORKER_MIN_TIME_PROP = "minSecTime";
    private static final String URL_WORKER_MAX_TIME_PROP = "maxSecTime";
    private static final String URL_WORKER_SENIOR_STATUS_PROP = "seniorStatus";


    public XMLKeeper() throws IOException, SAXException, ParserConfigurationException, TransformerException {
       getInitDocument();
    }

    @Override
    public String addSeniorUrlWorker(UrlWorker urlWorker) {
        if(urlWorker != null) {
            Document doc = null;
            try {
                doc = getInitDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc == null) {
                return null;
            }

            urlWorker.setSeniorStatus(true);
            return disassembleUrlWorker(doc, urlWorker);

        }
        return null;
    }

    @Override
    public String addVassalUrlWorker(UrlWorker urlWorker) {
        if(urlWorker != null) {
            Document doc = null;
            try {
                doc = getInitDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc == null) {
                return null;
            }

            urlWorker.setSeniorStatus(false);
            return disassembleUrlWorker(doc, urlWorker);

        }
        return null;
    }

    @Override
    public String removeUrlWorker(UrlWorker urlWorker) {
        if(urlWorker != null) {
            Document doc = null;
            try {
                doc = getInitDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc == null) {
                return null;
            }

            urlWorker.setSeniorStatus(true);

            if(doc != null && urlWorker != null && isWorkerValid(urlWorker))  {
                Node workersNode = getWorkerNodes(doc);
                removeUrlWorker(workersNode, urlWorker);

                try {
                    writeDocInFile(doc,new File(FILE_PATH + FILE_NAME));
                    return urlWorker.getUrl();
                } catch (TransformerException e) {
                    logger.error("Can't open init.xml.",e);
                    return null;
                }
            }

        }
        return null;
    }

    @Override
    public List<UrlWorker> getUrlWorkers() {
        Document doc = null;
        try {
            doc = getInitDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(doc == null) {
            return null;
        }

        Node workersNode = getWorkerNodes(doc);

        List<UrlWorker> workers = new ArrayList<UrlWorker>();
        if(workersNode != null) {
            NodeList workerNodes = workersNode.getChildNodes();
            for (int i = 0; i < workerNodes.getLength(); i++) {
                Node node = workerNodes.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType() && URL_WORKER.equals(node.getNodeName())) {
                    UrlWorker worker = assembleUrlWorker(node);
                    if(isWorkerValid(worker)) {
                        workers.add(worker);
                    } else {
                        workersNode.removeChild(node);
                    }
                }
            }
        }
        try {
            writeDocInFile(doc,new File(FILE_PATH + FILE_NAME));
        } catch (TransformerException e) {
            logger.error("Can't open init.xml.",e);
        }
        Collections.sort(workers,new Comparator<UrlWorker>() {
            @Override
            public int compare(UrlWorker o1, UrlWorker o2) {
                int res = 0;
                if(o1.getUrl() != null) {
                    res = o1.getUrl().compareTo(o2.getUrl());
                } else if(o2.getUrl() != null) {
                    res = -1;
                }
                if(o1.isSeniorStatus() && !o2.isSeniorStatus()) {
                    res = 1;
                } else if(!o1.isSeniorStatus() && o2.isSeniorStatus()) {
                    res = -1;
                }
                return res;
            }
        });
        return workers;
    }

    @Override
    public String getAutoCompleteTemplatesPath() {
        Document doc = null;
        try {
            doc = getInitDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(doc == null) {
            return null;
        }

        Node actNode = getACTNode(doc);
        if(actNode != null) {
            return actNode.getTextContent();
        }
        return null;
    }

    @Override
    public String setAutoCompleteTemplatesPath(String path) {
        if(path != null) {
            Document doc = null;
            try {
                doc = getInitDocument();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(doc == null) {
                return null;
            }

            Node rootNode = getRootNode(doc);
            NodeList rootChildren = rootNode.getChildNodes();
            for (int i = 0; i < rootChildren.getLength(); i++) {
                Node node = rootChildren.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType() && ACT.equals(node.getNodeName())) {
                    rootNode.removeChild(node);
                }
            }

            Element actElement = doc.createElement(ACT);
            actElement.setTextContent(path);
            rootNode.appendChild(actElement);

            try {
                writeDocInFile(doc,new File(FILE_PATH + FILE_NAME));
                return path;
            } catch (TransformerException e) {
                logger.error("Can't open init.xml.",e);
                return null;
            }
        }
        return null;
    }

    private Document getInitDocument() throws ParserConfigurationException, TransformerException, IOException {

        File initFile = new File(FILE_PATH + FILE_NAME);
        boolean fileCreated = false;
        if(!initFile.exists()) {
            initFile = new File(FILE_PATH + FILE_NAME);
            try {
                fileCreated = initFile.createNewFile();
            } catch (IOException e) {
                logger.error("Can't create init.xml.", e);
                throw e;
            }
        }
        Document doc = null;
        if(initFile.exists()) {
            try {

                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                try {

                    if(fileCreated) {
                        doc = docBuilder.newDocument();
                    } else {
                        doc = docBuilder.parse(initFile);
                    }

                } catch (SAXException e) {

                    logger.error("Incorrect init.xml. Rewrite file.", e);
                    PrintWriter writer = new PrintWriter(initFile);
                    writer.print("");
                    writer.close();
                    doc = docBuilder.newDocument();

                } catch (IOException e) {
                    logger.error("Can't create init.xml.", e);
                    throw e;
                }

                Node rootNode = getRootNode(doc);

                if(rootNode == null) {
                    Element rootElement = doc.createElement(ROOT_ELEMENT);
                    doc.appendChild(rootElement);
                    Element workersElement = doc.createElement(URL_WORKERS);
                    rootElement.appendChild(workersElement);
                } else {

                    Node workersNode = null;
                    NodeList rootChildren = rootNode.getChildNodes();
                    for (int i = 0; i < rootChildren.getLength(); i++) {
                        Node node = rootChildren.item(i);
                        if (Node.ELEMENT_NODE == node.getNodeType() && URL_WORKERS.equals(node.getNodeName())) {
                            workersNode = node;
                        }
                    }

                    if(workersNode == null) {
                        Element workersElement = doc.createElement(URL_WORKERS);
                        rootNode.appendChild(workersElement);
                    }
                }

                writeDocInFile(doc,initFile);

            } catch (ParserConfigurationException e) {
                logger.error(e);
                throw e;
            } catch (TransformerConfigurationException e) {
                logger.error(e);
                throw e;
            } catch (TransformerException e) {
                logger.error(e);
                throw e;
            }
        }  else {
            logger.error("Can't open init.xml.");
        }
        return doc;
    }

    private void writeDocInFile(Document doc, File file) throws TransformerException {
        // write the content into xml file
        try {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            logger.error(e);
            throw e;
        } catch (TransformerException e) {
            logger.error(e);
            throw e;
        }
    }

    private Node getWorkerNodes(Document doc) {
        if(doc!=null) {
            Node rootNode = getRootNode(doc);
            if(rootNode == null) {
                return null;
            } else {
                NodeList rootChildren = rootNode.getChildNodes();
                for (int i = 0; i < rootChildren.getLength(); i++) {
                    Node node = rootChildren.item(i);
                    if (Node.ELEMENT_NODE == node.getNodeType() && URL_WORKERS.equals(node.getNodeName())) {
                        return node;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private Node getACTNode(Document doc) {
        if(doc!=null) {
            Node rootNode = getRootNode(doc);
            if(rootNode == null) {
                return null;
            } else {
                NodeList rootChildren = rootNode.getChildNodes();
                for (int i = 0; i < rootChildren.getLength(); i++) {
                    Node node = rootChildren.item(i);
                    if (Node.ELEMENT_NODE == node.getNodeType() && ACT.equals(node.getNodeName())) {
                        return node;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private Node getRootNode(Document doc) {
        if(doc!=null) {
            NodeList roots = doc.getChildNodes();
            for (int i = 0; i < roots.getLength(); i++) {
                Node node = roots.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType() && ROOT_ELEMENT.equals(node.getNodeName())) {
                    return node;
                }
            }
        }
        return null;
    }

    private UrlWorker assembleUrlWorker(Node workerNode) {
        if(workerNode!=null) {
            UrlWorker worker = new UrlWorker();
            NodeList propertiesNodes = workerNode.getChildNodes();
            for (int i = 0; i < propertiesNodes.getLength(); i++) {
                Node node = propertiesNodes.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    if(URL_WORKER_URL_PROP.equals(node.getNodeName())) {
                        worker.setUrl(node.getTextContent());
                    } else if(URL_WORKER_MIN_COST_PROP.equals(node.getNodeName())) {
                        worker.setMinCost(getIntegerContent(node,worker.getMinCost()));
                    }  else if(URL_WORKER_MAX_COST_PROP.equals(node.getNodeName())) {
                        worker.setMaxCost(getIntegerContent(node, worker.getMaxCost()));
                    }  else if(URL_WORKER_PERCENT_PROP.equals(node.getNodeName())) {
                        worker.setPercent(getIntegerContent(node, worker.getPercent()));
                    }  else if(URL_WORKER_MIN_TIME_PROP.equals(node.getNodeName())) {
                        worker.setMinSecTime(getIntegerContent(node, worker.getMinSecTime()));
                    }  else if(URL_WORKER_MAX_TIME_PROP.equals(node.getNodeName())) {
                        worker.setMaxSecTime(getIntegerContent(node, worker.getMaxSecTime()));
                    }  else if(URL_WORKER_SENIOR_STATUS_PROP.equals(node.getNodeName())) {
                        worker.setSeniorStatus(getBooleanContent(node,worker.isSeniorStatus()));
                    }
                }
            }
            return worker;
        }
        return null;
    }

    private String disassembleUrlWorker(Document doc, UrlWorker urlWorker) {
        if(doc != null && urlWorker != null && isWorkerValid(urlWorker))  {
            Node workersNode = getWorkerNodes(doc);

            removeUrlWorker(workersNode, urlWorker);

            Element urlWorkerElement = doc.createElement(URL_WORKER);
            Element urlPropElement = doc.createElement(URL_WORKER_URL_PROP);
            urlPropElement.setTextContent(urlWorker.getUrl());
            urlWorkerElement.appendChild(urlPropElement);
            Element minCostPropElement = doc.createElement(URL_WORKER_MIN_COST_PROP);
            minCostPropElement.setTextContent(String.valueOf(urlWorker.getMinCost()));
            urlWorkerElement.appendChild(minCostPropElement);
            Element maxCostPropElement = doc.createElement(URL_WORKER_MAX_COST_PROP);
            maxCostPropElement.setTextContent(String.valueOf(urlWorker.getMaxCost()));
            urlWorkerElement.appendChild(maxCostPropElement);
            Element percentPropElement = doc.createElement(URL_WORKER_PERCENT_PROP);
            percentPropElement.setTextContent(String.valueOf(urlWorker.getPercent()));
            urlWorkerElement.appendChild(percentPropElement);
            Element minTimePropElement = doc.createElement(URL_WORKER_MIN_TIME_PROP);
            minTimePropElement.setTextContent(String.valueOf(urlWorker.getMinSecTime()));
            urlWorkerElement.appendChild(minTimePropElement);
            Element maxTimePropElement = doc.createElement(URL_WORKER_MAX_TIME_PROP);
            maxTimePropElement.setTextContent(String.valueOf(urlWorker.getMaxSecTime()));
            urlWorkerElement.appendChild(maxTimePropElement);
            Element seniorStatusPropElement = doc.createElement(URL_WORKER_SENIOR_STATUS_PROP);
            seniorStatusPropElement.setTextContent(String.valueOf(urlWorker.isSeniorStatus()));
            urlWorkerElement.appendChild(seniorStatusPropElement);

            workersNode.appendChild(urlWorkerElement);

            try {
                writeDocInFile(doc,new File(FILE_PATH + FILE_NAME));
                return urlWorker.getUrl();
            } catch (TransformerException e) {
                logger.error("Can't open init.xml.",e);
                return null;
            }
        }
        return null;
    }

    private void removeUrlWorker(Node workersNode, UrlWorker urlWorker) {
        if(workersNode != null && urlWorker != null) {
            if(workersNode != null) {
                NodeList workerNodes = workersNode.getChildNodes();
                for (int i = 0; i < workerNodes.getLength(); i++) {
                    Node workersChildren = workerNodes.item(i);
                    if (Node.ELEMENT_NODE == workersChildren.getNodeType() && URL_WORKER.equals(workersChildren.getNodeName())) {
                        NodeList propertiesNodes = workersChildren.getChildNodes();
                        for (int j = 0; j < propertiesNodes.getLength(); j++) {
                            Node workerChildren = propertiesNodes.item(j);
                            if (Node.ELEMENT_NODE == workerChildren.getNodeType() && URL_WORKER_URL_PROP.equals(workerChildren.getNodeName())) {
                                if(urlWorker.getUrl().equals(workerChildren.getTextContent())) {
                                    workersNode.removeChild(workersChildren);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private int getIntegerContent(Node node, int defaultValue) {
        String text = node.getTextContent();
        if(text!=null) {
            try{
            defaultValue = Integer.parseInt(text);
            } catch (NumberFormatException e) {
                logger.debug("Incorrect integer text in xml element.",e);
            }
        }
        return defaultValue;
    }

    private boolean getBooleanContent(Node node, boolean defaultValue) {
        String text = node.getTextContent();
        if(text!=null) {
            try{
                defaultValue = Boolean.parseBoolean(text);
            } catch (NumberFormatException e) {
                logger.debug("Incorrect boolean text in xml element.",e);
            }
        }
        return defaultValue;
    }

    private boolean isWorkerValid(UrlWorker worker) {
        if(worker!=null) {
            String url = worker.getUrl();
            if(url != null) {
                String urlRegEx = "http://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                if(url.matches(urlRegEx)) {
                    String checkedUrl = URLCheckUtil.checkUrl(url);
                    if(checkedUrl!=null) {
                        worker.setUrl(url);
                        return true;
                    }
                } else {
                    logger.debug("Incorrect URL " + worker.getUrl());
                }
            } else {
                logger.debug("Fail validation " + worker.getUrl());
            }
        } else {
            logger.debug("Fail validation UrlWorker null");
        }
        return false;
    }
}
