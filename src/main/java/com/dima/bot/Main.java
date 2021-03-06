package com.dima.bot;

import com.dima.bot.manager.executor.AdvertisementExtractor;
import com.dima.bot.manager.BotsManager;
import com.dima.bot.manager.executor.FerioAdvertisementExtractor;
import com.dima.bot.manager.model.Advertisement;
import com.dima.bot.settings.SettingsKeeper;
import com.dima.bot.settings.XMLKeeper;
import com.dima.bot.settings.model.UrlWorker;
import com.dima.bot.ui.ConfigurationPage;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String url = "http://www.ferio.ru/clad/?ch=orders&st=ok&agent=53983&cp=901726&brand=2&criterion=&day=&month=&year=&keywrd=&list=1";

        try {
            SettingsKeeper keeper = new XMLKeeper();
            BotsManager botsManager = new BotsManager(keeper);
            botsManager.runAutoFillDetector();
            botsManager.runExecutedAdvertisementDetector();
            new ConfigurationPage(botsManager);
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
