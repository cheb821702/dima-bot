package com.dima.bot;

import com.dima.bot.executor.BotsManager;
import com.dima.bot.settings.SettingsKeeper;
import com.dima.bot.settings.XMLKeeper;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.ui.ConfigurationPage;
import com.dima.bot.util.HttpManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String url = "http://www.ferio.ru/clad/?ch=orders&st=ok&agent=53983&cp=901726&brand=2&criterion=&day=&month=&year=&keywrd=&list=1";

        HttpManager httpManager = new HttpManager();

        String page = httpManager.doGet(url);

        try {
            SettingsKeeper keeper = new XMLKeeper();
            System.out.println(keeper.getAutoCompleteTemplatesPath());
            keeper.addSeniorUrlWorker(new UrlWorker("Http:1",1000,5000,20,15,120));
            keeper.addVassalUrlWorker(new UrlWorker(url, 650, 3000, 20, 0, 50));
            keeper.removeUrlWorker(new UrlWorker("Http:1",1000,5000,20,15,120));
            List<UrlWorker> list = keeper.getUrlWorkers();
            for (UrlWorker worker : list) {
                System.out.println(worker.toString());
            }
            new ConfigurationPage(new BotsManager(keeper));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}