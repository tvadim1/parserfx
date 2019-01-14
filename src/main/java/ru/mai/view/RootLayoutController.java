package ru.mai.view;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.mai.db.DataMapperImplementation;
import ru.mai.db.IDataMapper;
import ru.mai.db.IOData;
import ru.mai.utils.DocReader;
import ru.mai.model.Data;

public class RootLayoutController implements Initializable{
    //doc
    private DocReader dr;
    private String inputText = "";
    private String sentences[];
    //db
    private IDataMapper mapper = new DataMapperImplementation();
    private IOData newObject;
    //parser
    private String input = "";
    private List<Data>parsedText = new ArrayList<>();

    private String line = "------------------------------------------------------------------------------------------------------\n";

    @FXML
    private TextField tf;
    @FXML
    private TextArea ta;
    @FXML
    private Button bPT;
    @FXML
    private Button bPS;
    @FXML
    private Button bSS;
    @FXML
    private ComboBox<String>comboBox;

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setTitle("Открыть файл");//Заголовок диалога
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("*.doc", "*.doc");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("*.docx", "*.docx")
        );
        //Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println("Файл открыт: " + file.toString());
        }
        else {
            System.out.println("Файл не удачно открылся");
        }
        //этап чтения из .doc
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file.toString()));
            try {
                //System.out.println(dr.read(is));
                inputText = dr.read(is);
            } catch (Exception e) { e.printStackTrace(); }
            is.close();
        } catch (IOException e) { e.printStackTrace(); }
        //этап разбора
        sentences = inputText.split("[.!?]\\s*");
        //вывод инф
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Parser FX");
        alert.setHeaderText(null);
        alert.setContentText("Текст загружен");
        alert.showAndWait();
        //кнопки
        bPS.setDisable(false);
        bPT.setDisable(false);
        bSS.setDisable(false);
    }
    @FXML
    private void handleParseText() {
        parsedText.clear();
        for (int i = 0; i < sentences.length; ++i) {
            makeRequest(sentences[i]);
        }
        //вставка в БД
        for (Data data : parsedText){
            if (data.getDep().equals("отрицательная частица не")){mapper.insertTable(data.getDep(), "NE");}
            else if (data.getDep().equals("вспомогательное сказуемое")){mapper.insertTable(data.getDep(), "AUXSKAZ");}
            else if (data.getDep().equals("часть составного сказуемого")){mapper.insertTable(data.getDep(), "CHASTSKAZ");}
            else if (data.getDep().equals("числительное")){mapper.insertTable(data.getDep(), "CHISLIT");}
            else if (data.getDep().equals("дополнение")){mapper.insertTable(data.getDep(), "DOPOL");}
            else if (data.getDep().equals("междометие")){mapper.insertTable(data.getDep(), "MEJD");}
            else if (data.getDep().equals("обращение")){mapper.insertTable(data.getDep(), "OBRASH");}
            else if (data.getDep().equals("обстоятельство")){mapper.insertTable(data.getDep(), "OBST");}
            else if (data.getDep().equals("определение")){mapper.insertTable(data.getDep(), "OPREDEL");}
            else if (data.getDep().equals("подлежащее")){mapper.insertTable(data.getDep(), "PODL");}
            else if (data.getDep().equals("подчинительный союз")){mapper.insertTable(data.getDep(), "PODSOUZ");}
            else if (data.getDep().equals("предлог")){mapper.insertTable(data.getDep(), "PREDLOG");}
            else if (data.getDep().equals("сказуемое")){mapper.insertTable(data.getDep(), "SKAZ");}
            else if (data.getDep().equals("союз")){mapper.insertTable(data.getDep(), "SOUZ");}
            else if (data.getDep().equals("устойчивое выражение")){mapper.insertTable(data.getDep(), "USTVIR");}
        }
    }

    @FXML
    private void handleParseSelected() {
        for (Data data : parsedText){
            if (comboBox.getValue().toString().equals(data.getDep())){
                ta.appendText(data.getWord()+"\t->\t"+data.getDep()+"\n");
            }
        }
        ta.appendText(line);
    }

    @FXML
    private void handleShowStats() {
        if (comboBox.getValue().toString().equals("отрицательная частица не")){mapper.selectCount(ta, "NE");}
        else if (comboBox.getValue().toString().equals("вспомогательное сказуемое")){mapper.selectCount(ta, "AUXSKAZ");}
        else if (comboBox.getValue().toString().equals("часть составного сказуемого")){mapper.selectCount(ta, "CHASTSKAZ");}
        else if (comboBox.getValue().toString().equals("числительное")){mapper.selectCount(ta, "CHISLIT");}
        else if (comboBox.getValue().toString().equals("дополнение")){mapper.selectCount(ta, "DOPOL");}
        else if (comboBox.getValue().toString().equals("междометие")){mapper.selectCount(ta, "MEJD");}
        else if (comboBox.getValue().toString().equals("обращение")){mapper.selectCount(ta, "OBRASH");}
        else if (comboBox.getValue().toString().equals("обстоятельство")){mapper.selectCount(ta, "OBST");}
        else if (comboBox.getValue().toString().equals("определение")){mapper.selectCount(ta, "OPREDEL");}
        else if (comboBox.getValue().toString().equals("подлежащее")){mapper.selectCount(ta, "PODL");}
        else if (comboBox.getValue().toString().equals("подчинительный союз")){mapper.selectCount(ta, "PODSOUZ");}
        else if (comboBox.getValue().toString().equals("предлог")){mapper.selectCount(ta, "PREDLOG");}
        else if (comboBox.getValue().toString().equals("сказуемое")){mapper.selectCount(ta, "SKAZ");}
        else if (comboBox.getValue().toString().equals("союз")){mapper.selectCount(ta, "SOUZ");}
        else if (comboBox.getValue().toString().equals("устойчивое выражение")){mapper.selectCount(ta, "USTVIR");}
    }
    @FXML
    public void handleParse(){
        input = tf.getText();
        if (input.equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Parser FX");
            alert.setHeaderText(null);
            alert.setContentText("Введите текст");
            alert.showAndWait();
        }
        else {
            parsedText.clear();
            makeRequest(input);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ta.setEditable(false);
        tf.setText("Корабли уходят в море.");
        bPS.setDisable(true);
        bPT.setDisable(true);
        bSS.setDisable(true);

        comboBox.getItems().addAll(
        "отрицательная частица не",
                "определение",
                "дополнение",
                "подлежащее",
                "обстоятельство",
                "сказуемое",
                "часть составного сказуемого",
                "вспомогательное сказуемое",
                "числительное",
                "предлог",
                "союз",
                "подчинительный союз",
                "обращение",
                "междометие",
                "устойчивое выражение"
        );

    }

    private void makeRequest(String input) {
        String output = null;
        try {
            //get
            Client client = Client.create();
            WebResource webResource = client.resource("http://localhost:9000/api/v1/use/Russian");
            ClientResponse response = webResource.accept("application/json;charset=UTF-8").get(ClientResponse.class);
            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
            }
            output = response.getEntity(String.class);
            System.out.println("Output from Server:");
            System.out.println(output);
            //post
            webResource = client.resource("http://localhost:9000/api/v1/query");
            String inputj = "{\"strings\":[\""+input+"\"],\"tree\":false}";
            response = webResource.type("application/json; charset=UTF-8").post(ClientResponse.class, inputj);
            System.out.println("Output from Server:");
            output = cut(response.getEntity(String.class));
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject mainObj = new JSONObject(output);
        JSONArray jsonArray = (JSONArray) mainObj.get("output");
        for (int i = 0; i <jsonArray.length(); i++) {
            JSONObject obj = (JSONObject) jsonArray.get(i);
            String word = (String) obj.get("word");
            String dep = (String) obj.get("dep");
            String polarity = "";
            if(obj.has("polarity")) {
                polarity = (String) obj.get("polarity");
            }
            String fpos = (String) obj.get("fpos");

            if (polarity.equals("Neg")){ dep = "отрицательная частица не"; }
            else if (fpos.contains("VERB")){ dep = "сказуемое"; }
            else if (dep.contains("nummod")){dep = "числительное";}
            else if (dep.contains("nsubj")){ dep = "подлежащее"; }
            else if (dep.contains("root")){ dep = "сказуемое"; }
            else if (dep.contains("case")){ dep = "предлог"; }
            else if (dep.contains("obl")){ dep = "обстоятельство"; }
            else if (dep.contains("punct")){ dep = "знак препинания"; }
            else if (dep.contains("nmod")){ dep = "дополнение"; }
            else if (dep.contains("cop")){ dep = "часть составного сказуемого"; }
            else if (dep.contains("advmod")){ dep = "обстоятельство"; }
            else if (dep.contains("amod")){ dep = "определение"; }
            else if (dep.contains("iobj")){ dep = "дополнение"; }
            else if (dep.contains("cc")){ dep = "союз"; }
            else if (dep.contains("conj")){ dep = "в однородной связи через союз"; }
            else if (dep.contains("xcomp")){ dep = "сказуемое"; }
            else if (dep.contains("obj")){ dep = "дополнение"; }
            else if (dep.contains("csubj")){ dep = "сказуемое"; }
            else if (dep.contains("mark")){ dep = "подчинительный союз"; }
            else if (dep.contains("vocative")){ dep = "обращение"; }
            else if (dep.contains("expl")){ dep = "обстоятельство"; }
            else if (dep.contains("dislocated")){ dep = "дислокация"; }
            else if (dep.contains("advcl")){ dep = "сказуемое"; }
            else if (dep.contains("discourse")){ dep = "междометие"; }
            else if (dep.contains("aux")){ dep = "вспомогательное сказуемое"; }
            else if (dep.contains("appos")){ dep = "обращение(сокращение)"; }
            else if (dep.contains("acl")){ dep = "определение (конструкция)"; }
            else if (dep.contains("det")){ dep = "определение"; }
            else if (dep.contains("clf")){ dep = "дополнение"; }
            else if (dep.contains("fixed")){ dep = "устойчивое выражение"; }
            else if (dep.contains("flat")){ dep = "дополнение"; }
            else if (dep.contains("compound")){ dep = "сказуемое"; }
            else if (dep.contains("list")){ dep = "элемент списка"; }
            else if (dep.contains("orphan")){ dep = "дополнение"; }
            else if (dep.contains("goeswith")){ dep = "предлог"; }
            else if (dep.contains("reparandum")){ dep = "обстоятельство"; }
            else if (dep.contains("dep")){ dep = "Не определено!"; }

            parsedText.add(new Data(word, dep));
            ta.appendText(word+"\t->\t"+dep+"\n");
        }
        /*for (Data data : parsedText){
            ta.appendText(data.getWord()+"\t->\t"+data.getDep()+"\n");
        }*/
        ta.appendText(line);
    }

    private String cut(String in){
        return in.substring(in.indexOf("[{\"inp")+1, in.indexOf("]}]")+2);
    }

    @FXML
    public void handleSelectAll(){
        /*TestConnect tc = new TestConnect();
        tc.start();*/

        ta.appendText("select * from plain;\n");

        /* вывод */
        mapper.selectAll(ta);
        ta.appendText(line);
    }

    @FXML
    public void handleInsert(){
        String ccstr = "";
        for (Data data : parsedText){
            ccstr += data.getWord()+"->"+data.getDep() + ", ";
        }
        System.out.println("concat_string="+ccstr);
        newObject = new IOData(input, ccstr);

        /* добавление */
        mapper.insert(newObject, ta);
        ta.appendText(line);


//        /* поиск */
//        Optional<IOData>searchObject = mapper.find(newObject.getX());
//        System.out.println("найдено для x = такому-то: y = "+ searchObject.get().getY());
//
//        /* обновление */
//        newObject = new IOData(newObject.getX(), 1.8);
//        mapper.update(newObject);
//        System.out.println("обновлено");
//
//        /* удаление */
//        mapper.delete(newObject);
//        System.out.println("удалено");
    }
}

