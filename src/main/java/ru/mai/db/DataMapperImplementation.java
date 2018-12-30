package ru.mai.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.TextArea;
import ru.mai.db.connection.DatabaseConnection;

public final class DataMapperImplementation implements IDataMapper {
    private List<IOData>arrayList = new ArrayList<>();

    @Override
    public Optional<IOData> find(String x) {
        for (final IOData data : this.getArrayList()) {
            if (data.getX() == x) {
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    @Override
    public void update(IOData xyObject) throws DataMapperException {
        if (this.getArrayList().contains(xyObject)) {
            final int index = this.getArrayList().indexOf(xyObject);
            this.getArrayList().set(index, xyObject);
        } else {
            //throw new DataMapperException("not found");
        }
    }

    @Override
    public void insert(IOData xyObject, TextArea ta) throws DataMapperException {
        /*if (!this.getArrayList().contains(xyObject)) {
            this.getArrayList().add(xyObject);
        } else {
            throw new DataMapperException("already exists");
        }*/
        Statement statement = null;
        try {
            DatabaseConnection.getInstance();
            statement = DatabaseConnection.getInstance().getConnection().createStatement();

            String sql = "insert into plain (`input`, `output`) values(\""+xyObject.getX()+"\",\""+xyObject.getY()+"\");";
            ta.appendText(sql+"\n");
            statement.executeUpdate(sql);
        } catch (SQLException e) {
        }finally{
            try {
                DatabaseConnection.getInstance().getConnection().close();
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public void delete(IOData xyObject) throws DataMapperException {
        if (this.getArrayList().contains(xyObject)) {
            this.getArrayList().remove(xyObject);
        } else {
            //throw new DataMapperException("not found");
        }
    }

    @Override
    public void selectAll(TextArea ta) throws DataMapperException{
        Statement statement = null;
        try {
            DatabaseConnection.getInstance();
            statement = DatabaseConnection.getInstance().getConnection().createStatement();

            ResultSet rs = statement.executeQuery("select * from plain");

            while(rs.next()){
                ta.appendText("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
                ta.appendText("input: " + rs.getString(2)+"\n");
                ta.appendText("output: " + rs.getString(3)+"\n");
                ta.appendText("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            }
        } catch (SQLException e) {
        }finally{
            try {
                DatabaseConnection.getInstance().getConnection().close();
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

    public List<IOData>getArrayList() {
        return this.arrayList;
    }
}
/*
CREATE TABLE PLAIN (
    id int NOT NULL AUTO_INCREMENT,
    input varchar(1024),
    output varchar(1024),		//у данных варчаров нужно поменять кодировку еще на utf-32-ci, например
    PRIMARY KEY (ID)
);
 */
