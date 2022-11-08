package ej1;

import com.thoughtworks.xstream.XStream;
import ej1.modelos.Animal;
import ej1.streams.MyOos;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static ArrayList<Animal> lista;
    public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException, SAXException {
        File fileBin = new File("animales.dat");
        File fileXml = new File("animales.txt");
        lista = new ArrayList<>();
        Animal animal1 = new Animal("a", "a", 1, "a");
        Animal animal2 = new Animal("a", "a", 2, "a");
        lista.add(animal1);
        lista.add(animal2);


        try {
            escribirBin(fileBin);
            cargarBin(fileBin);
            escribirXML(fileXml);
            cargarXML(fileXml);
        } catch (EOFException e){
            throw e;
        }
        catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static Animal crearAnimal() {
        System.out.println("Especie");
        String especie = sc.nextLine();
        System.out.println("Raza");
        String raza = sc.nextLine();
        System.out.println("Edad");
        int edad = sc.nextInt();
        sc.nextLine();
        System.out.println("Color");
        String color = sc.nextLine();

        return new Animal(especie, raza, edad, color);
    }

    private static void escribirBin(File fileBin) throws IOException {


        ObjectOutputStream oos;
        if(fileBin.exists()){
            oos = new MyOos(new FileOutputStream(fileBin, true));
        }
        else {
            oos = new ObjectOutputStream(new FileOutputStream(fileBin));
        }

        for (int i = 0; i < lista.size(); i++) {
            Animal animal = lista.get(i);
            oos.writeObject(animal);
        }

        oos.close();
    }

    private static void cargarBin(File fileBin) throws IOException, ClassNotFoundException {
        if (fileBin.exists()){
            FileInputStream inputStream = new FileInputStream(fileBin);
            ObjectInputStream ois = new ObjectInputStream(inputStream);
            lista.clear();
            while (true){
                Animal animal = (Animal) ois.readObject();
                lista.add(animal);
                System.out.println(animal.toString());
            }
        }
    }

    private static void cargarXML(File fileXml) throws ParserConfigurationException, IOException, SAXException {
// Parsear fichero XML -> NO LEO PASO A PASO EL FICHERO
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        // Ahora cargo dentro de una variable Documento (DOM)
        Document document = db.parse(fileXml);
        // Limpio los nodos para una mejor visualización
        document.getDocumentElement().normalize();

        System.out.println("Raíz del documento: " + document.getDocumentElement().getNodeName());

        // NodeList nodos = document.getChildNodes();
        NodeList nodos = document.getElementsByTagName("animal");

        for (int i = 0; i < nodos.getLength(); i++) {

            Node nodo = nodos.item(i); // Extraigo el nodo
            System.out.println("Elemento: " + nodo.getNodeName());
            XStream xStream = new XStream();
            xStream.allowTypesByWildcard(new String[]{"modelos.*"});

            // Compruebo que tienen hijos
            if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                Animal animal;
                Element element = (Element) nodo;

                String especie = element.getElementsByTagName("especie").item(0).getTextContent();
                String raza = element.getElementsByTagName("raza").item(0).getTextContent();
                int edad = Integer.parseInt(element.getElementsByTagName("edad").item(0).getTextContent());
                String color = element.getElementsByTagName("color").item(0).getTextContent();

                animal = new Animal(especie, raza, edad, color);
                System.out.println(animal);

            }
        }

    }

    private static void escribirXML(File fileXml) throws IOException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();

        //Creo un elemento
        Element raiz =  document.createElement("Animales");
        //Se lo enchufo al padre
        document.appendChild(raiz);
        for (int i = 0; i < lista.size(); i++) {
            Animal object = lista.get(i);
            Element animal = document.createElement("animal");
            raiz.appendChild(animal);
            Element especie = document.createElement("especie");
            animal.appendChild(especie);
            especie.setTextContent(object.getEspecie());
            Element raza = document.createElement("raza");
            animal.appendChild(raza);
            raza.setTextContent(object.getRaza());
            Element edad = document.createElement("edad");
            animal.appendChild(edad);
            edad.setTextContent(String.valueOf(object.getEdad()));
            Element color = document.createElement("color");
            animal.appendChild(color);
            color.setTextContent(object.getRaza());
        }
        // creamos un traductor de elements a dom
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer optimus = tf.newTransformer();
        DOMSource ds = new DOMSource(document);

        // organizar documento
        optimus.setOutputProperty(OutputKeys.INDENT,"yes");

        StreamResult result = new StreamResult(new File("estudiantes.xml"));
        optimus.transform(ds, result);
        
    }

    private static int menu() {
        System.out.println("1. Crear Animal");
        System.out.println("2. Escribir Lista en fichero binario");
        System.out.println("3. Cargar de fichero binario");
        System.out.println("4. Escribir lista en XML (DOM/SAX indiferente)");
        System.out.println("5. Cargar de fichero XML (DOM/SAX indiferente)");

        return sc.nextInt();
    }


}