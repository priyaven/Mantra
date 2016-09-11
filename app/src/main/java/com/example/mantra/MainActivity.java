package com.example.mantra;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class MainActivity extends AppCompatActivity {

    public static final String MANTRATITLE = "mantraTitle";
    public static final String MANTRATEXT = "mantraText";
    public static final String MANTRACOUNT = "mantraCount";
    public static final String MANTRAPOS = "mantraPos";

    private final String profile_fn = "user_profile.xml";
    ListView listView ;
    private List<MantraObject> mantraList = null;


    private String[] mantraListToTitleArray(MantraObject[] mantraObjects){
        String[] titleArray = new String[mantraObjects.length];
        for(int i=0;i<mantraObjects.length;i++){
            titleArray[i] = mantraObjects[i].getTitle();
        }
        return titleArray;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);

        Context context = getApplicationContext();
        File appdir = context.getFilesDir();
        File profile_file = new File(appdir.getAbsolutePath(),profile_fn);
        if(true/*!profile_file.exists()*/){
            InputStream inputStream = context.getResources().openRawResource(R.raw.profile_local);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(profile_file);
                byte buf[]=new byte[1024];
                int len;
                while((len=inputStream.read(buf))>0) {
                    fileOutputStream.write(buf,0,len);
                }
                fileOutputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(profile_file);
            NodeList nList = doc.getElementsByTagName("item");
            this.mantraList = new ArrayList<MantraObject>();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    this.mantraList.add(new MantraObject(nNode));
                }
            }

            Intent mantraIntent = getIntent();
            boolean isCancel = mantraIntent.getBooleanExtra("IsCancel",true);
            if(!isCancel){
                boolean isSave = mantraIntent.getBooleanExtra("IsSave",false);
                if(isSave){
                    String newText =  mantraIntent.getStringExtra("newText");
                    int newCount = mantraIntent.getIntExtra("newCount",0);
                    int mantraPos = mantraIntent.getIntExtra("mantraPos",-1);
                    String newTitle = mantraIntent.getStringExtra("newTitle");
                    if(mantraPos == -1){
                        MantraObject mantraObject = new MantraObject();
                        mantraObject.setCount(newCount);
                        mantraObject.setText(newText);
                        mantraObject.setTitle(newTitle);
                        try {
                            doc.appendChild(mantraObject.toNodeObject());
                        } catch(DOMException e){
                            //TODO fix this. Catching the exception makes it ok in this sesson. doesnt change xml.
                            System.out.println(e.getMessage());
                            System.out.println(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        this.mantraList.add(mantraObject);

                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        Result output = new StreamResult(profile_file);
                        Source input = new DOMSource(doc);
                        transformer.transform(input, output);
                    } else {
                        MantraObject mantraObject = this.mantraList.get(mantraPos);
                        mantraObject.setCount(newCount);
                        mantraObject.setText(newText);
                        mantraObject.setTitle(newTitle);
                        Node oldnode = nList.item(mantraPos);
                        try {
                            oldnode.getParentNode().replaceChild(mantraObject.toNodeObject(), oldnode);
                        } catch(DOMException e){
                            //TODO fix this. Catching the exception makes it ok in this sesson. doesnt change xml.
                            System.out.println(e.getMessage());
                            System.out.println(e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        this.mantraList.set(mantraPos,mantraObject);

                        Transformer transformer = TransformerFactory.newInstance().newTransformer();
                        Result output = new StreamResult(profile_file);
                        Source input = new DOMSource(doc);
                        transformer.transform(input, output);
                    }
                }
            }

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Fourth - the Array of data

            MantraObject[] mantraobjx = new MantraObject[this.mantraList.size()];
            this.mantraList.toArray(mantraobjx);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, mantraListToTitleArray(mantraobjx));
            // Assign adapter to ListView
            listView.setAdapter(adapter);

            // ListView Item Click Listener
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // ListView Clicked item index
                    int itemPosition = position;
                    // ListView Clicked item value
                    if(parent.getContext() instanceof MainActivity) {
                        List<MantraObject> mantraObjects = ((MainActivity) parent.getContext()).mantraList;
                        MantraObject clickedMantra = mantraObjects.get(itemPosition);
                        Toast.makeText(getApplicationContext(),
                                clickedMantra.getText() , Toast.LENGTH_LONG)
                                .show();
                        Intent myIntent = new Intent(parent.getContext(), MantraActivity.class);
                        myIntent.putExtra(MainActivity.MANTRATITLE, clickedMantra.getTitle());
                        myIntent.putExtra(MainActivity.MANTRATEXT, clickedMantra.getText());
                        myIntent.putExtra(MainActivity.MANTRACOUNT,clickedMantra.getCount());
                        myIntent.putExtra(MainActivity.MANTRAPOS, itemPosition);
                        parent.getContext().startActivity(myIntent);
                    }

                }

            });

            Button newButton = (Button)findViewById(R.id.createnew);
            newButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("In new activity");
                    Intent myIntent = new Intent(MainActivity.this,MantraActivity.class);
                    myIntent.putExtra(MainActivity.MANTRATITLE, "New Mantra");
                    myIntent.putExtra(MainActivity.MANTRATEXT, "");
                    myIntent.putExtra(MainActivity.MANTRACOUNT,0);
                    myIntent.putExtra(MainActivity.MANTRAPOS, -1);
                    MainActivity.this.startActivity(myIntent);


                }
            });

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }
}
