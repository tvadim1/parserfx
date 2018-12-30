package ru.mai.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.org.apache.xpath.internal.operations.Neg;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.mai.db.DataMapperImplementation;
import ru.mai.db.IDataMapper;
import ru.mai.db.IOData;
import ru.mai.db.connection.TestConnect;
import ru.mai.model.Data;

public class RootLayoutController implements Initializable{
    //db
    private IDataMapper mapper = new DataMapperImplementation();
    private IOData newObject;
    //parser
    private String input = "";
    private List<Data>parsedText = new ArrayList<>();

    @FXML
    private TextField tf;
    @FXML
    private TextArea ta;

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
            makeRequest(input);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ta.setEditable(false);
        tf.setText("Корабли уходят в море.");
    }

    private void makeRequest(String input) {
        parsedText.clear();
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
        }
        for (Data data : parsedText){
            ta.appendText(data.getWord()+"\t->\t"+data.getDep()+"\n");
        }
        ta.appendText("--------------------------------------------------------------------------------------------------------\n");
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
        ta.appendText("--------------------------------------------------------------------------------------------------------\n");
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
        ta.appendText("--------------------------------------------------------------------------------------------------------\n");


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

