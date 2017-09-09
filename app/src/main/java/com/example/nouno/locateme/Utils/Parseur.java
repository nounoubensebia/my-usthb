package com.example.nouno.locateme.Utils;

import com.example.nouno.locateme.Activities.EmploiDuTempsActivity;
import com.example.nouno.locateme.Data.Creneau;
import com.example.nouno.locateme.Data.DoneCreneau;
import com.example.nouno.locateme.Data.DoneJour;
import com.example.nouno.locateme.Data.Jour;
import com.example.nouno.locateme.Data.Seance;
import com.example.nouno.locateme.Fragments.AgendaFragment;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by nouno on 30/06/2017.
 */

public class Parseur{

    // Parseur.parseXml(getAssets().open("test.xml"));
    //test.xml est le nom du fichier xml


    public static ArrayList<Jour> jours=new ArrayList<Jour>();



    public static void parseXml(String is) throws Exception {
        //String xmlFile = "C:/Users/user/Desktop/read.xml";
        // création d'une fabrique de documents (usine à fabriquer des analyseurs (parseurs) DOM)
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource in = new InputSource(new StringReader(is));

        // lecture du contenu du fichier XML
        Document document = builder.parse(in);
        //****************************getElementsByTagName()****************************************************//
        //récupération de la racine du document XML
        Element liste = document.getDocumentElement();
        //récupération des éléments "eleve"
        NodeList lignes = liste.getElementsByTagName("ligne");
        NodeList sums=liste.getElementsByTagName("sum");
        AgendaFragment.sum=sums.item(0).getFirstChild().getNodeValue();
        //System.out.println(lignes.getLength());1

        for (int i = 0;i<lignes.getLength();i++){
            Jour jour=new Jour();

            //Le nom du jour de la semaine
            jour.setNom(lignes.item(i).getChildNodes().item(0).getFirstChild().getNodeValue());
            //  System.out.println(lignes.item(i).getChildNodes().item(0).getFirstChild().getNodeValue());
            Element cren=(Element) lignes.item(i);
            // tableau des creneaux
            NodeList creneaux=cren.getElementsByTagName("creneau");


            for(int j=0;j<creneaux.getLength();j++)
            {
                Creneau creneau=new Creneau();
                for(int x=0;x<creneaux.item(j).getChildNodes().getLength();x++)
                {
                    if(creneaux.item(j).getChildNodes().item(x).getNodeName().equals("horaire"))
                    {

                        creneau.horaire=creneaux.item(j).getChildNodes().item(x).getFirstChild().getNodeValue();

                    }
                }


                Element sea=(Element) creneaux.item(j);
                // tableau des seances
                NodeList seances=sea.getElementsByTagName("seance");
                //    System.out.println("Le nombre de séances de ce créneau est = "+seances.getLength());

                for(int k=0;k<seances.getLength();k++)
                {
                    Node n=seances.item(k);
                    //pour chaque séance on fait le traitement suivant
                    Seance seance=new Seance();
                    for(int l=0;l<n.getChildNodes().getLength();l++)
                    {

                        Node e=n.getChildNodes().item(l);

                        if (e.hasChildNodes()) {

                            if(e.getNodeName().equals("type"))
                            {
                                seance.setType(e.getFirstChild().getNodeValue());
                            }
                            if(e.getNodeName().equals("module"))
                            {
                                seance.setModule(e.getFirstChild().getNodeValue());
                            }
                            if(e.getNodeName().equals("local"))
                            {
                                seance.setLocal(e.getFirstChild().getNodeValue());
                            }
                            if(e.getNodeName().equals("groupe"))
                            {
                                seance.setGroupe(e.getFirstChild().getNodeValue());

                            }
                            if(e.getNodeName().equals("enseignant"))
                            {
                                seance.setEnseignant(e.getFirstChild().getNodeValue());
                            }
                        }
                    }
                    creneau.setSeance(seance);
                }
                jour.setCreneau(creneau);

            }
            jours.add(jour);

        }


        for(int i=0;i<jours.size();i++)
        {
            System.out.println(jours.get(i).nom);
            for(int j=0;j<jours.get(i).creneaux.size();j++)
            {
                System.out.println(jours.get(i).creneaux.get(j).horaire);
                jours.get(i).creneaux.get(j).seance.remove(null);
                for(int k=0;k<jours.get(i).creneaux.get(j).seance.size();k++)
                {
                    //if(!jours.get(i).creneaux.get(j).seance.get(k).module.equals(null))
                    System.out.println(jours.get(i).creneaux.get(j).seance.get(k).module);
                }
            }

        }
    }

    public static ArrayList<DoneJour> getJours (String is) throws Exception {
        ArrayList<DoneJour> doneJours = new ArrayList<>();


        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource in = new InputSource(new StringReader(is));


        Document document = builder.parse(in);

        Element liste = document.getDocumentElement();

        NodeList lignes = liste.getElementsByTagName("ligne");
        NodeList sums = liste.getElementsByTagName("sum");
        AgendaFragment.sum = sums.item(0).getFirstChild().getNodeValue();


        for (int i = 0; i < lignes.getLength(); i++) {



            //jour.setNom(lignes.item(i).getChildNodes().item(0).getFirstChild().getNodeValue());
            String name = lignes.item(i).getChildNodes().item(0).getFirstChild().getNodeValue();

            Element cren = (Element) lignes.item(i);
            ArrayList<DoneCreneau> creneaus = new ArrayList<>();

            NodeList creneaux = cren.getElementsByTagName("creneau");

            String horaire = null;
            for (int j = 0; j < creneaux.getLength(); j++) {

                for (int x = 0; x < creneaux.item(j).getChildNodes().getLength(); x++) {
                    if (creneaux.item(j).getChildNodes().item(x).getNodeName().equals("horaire")) {

                        horaire = creneaux.item(j).getChildNodes().item(x).getFirstChild().getNodeValue();

                    }
                }


                Element sea = (Element) creneaux.item(j);
                // tableau des seances
                NodeList seances = sea.getElementsByTagName("seance");
                //    System.out.println("Le nombre de séances de ce créneau est = "+seances.getLength());
                ArrayList<Seance> seances1 = new ArrayList<>();
                for (int k = 0; k < seances.getLength(); k++) {
                    Node n = seances.item(k);

                    //pour chaque séance on fait le traitement suivant
                    Seance seance = new Seance();
                    for (int l = 0; l < n.getChildNodes().getLength(); l++) {

                        Node e = n.getChildNodes().item(l);

                        if (e.hasChildNodes()) {

                            if (e.getNodeName().equals("type")) {
                                seance.setType(e.getFirstChild().getNodeValue());
                            }
                            if (e.getNodeName().equals("module")) {
                                seance.setModule(e.getFirstChild().getNodeValue());
                            }
                            if (e.getNodeName().equals("local")) {
                                seance.setLocal(e.getFirstChild().getNodeValue());
                            }
                            if (e.getNodeName().equals("groupe")) {
                                seance.setGroupe(e.getFirstChild().getNodeValue());

                            }
                            if (e.getNodeName().equals("enseignant")) {
                                seance.setEnseignant(e.getFirstChild().getNodeValue());
                            }

                        }

                    }
                    seances1.add(seance);

                }
                DoneCreneau doneCreneau = new DoneCreneau(horaire,seances1);
                creneaus.add(doneCreneau);


            }
            doneJours.add(new DoneJour(creneaus,name));

        }
        return doneJours;
    }
}
