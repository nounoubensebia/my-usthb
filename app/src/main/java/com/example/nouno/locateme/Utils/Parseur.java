package com.example.nouno.locateme.Utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by nouno on 30/06/2017.
 */

public class Parseur{

    // Parseur.parseXml(getAssets().open("test.xml"));
    //test.xml est le nom du fichier xml


    public static ArrayList<String> horaire=new ArrayList<String>();

    public static void parseXml(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
        Recurse(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream).getDocumentElement());
    }
    public static void Recurse (Node n){
        for (int o=0;o<n.getChildNodes().getLength();o++)
            if(n.getChildNodes().item(o).getNodeName()!= "#text"){
                Node e = n.getChildNodes().item(o);
                if(e.hasChildNodes())
                    if(e.getNodeName().equals("horaire"))
                        //System.out.println(e.getNodeName()+"\t"+e.getFirstChild().getNodeValue());
                        horaire.add(e.getFirstChild().getNodeValue());
                    else

                        for(int x=0;x<e.getAttributes().getLength();x++){
                            Attr attr = (Attr) e.getAttributes().item(x);

                        }
                Recurse(e);
            }
    }
}
