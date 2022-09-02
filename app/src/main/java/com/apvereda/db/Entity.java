package com.apvereda.db;

import android.util.Log;

import com.apvereda.utils.DigitalAvatar;
import com.couchbase.lite.Array;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Dictionary;
import com.couchbase.lite.Document;
import com.couchbase.lite.Expression;
import com.couchbase.lite.Meta;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.MutableDictionary;
import com.couchbase.lite.MutableDocument;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryBuilder;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.SelectResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Entity extends AbstractEntity{
    private String uid = null;
    private String type;
    private Map<String, Value> values;

    public Entity(String uid, String name, String type, String[] privacy, Date timestamp, Map<String, Value> values){
        super(timestamp, privacy);
        setName(name);
        this.type=type;
        setUid(uid);
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public Map<String, Value> getValues() {
        return values;
    }

    public void setValues(Map<String,Value> values) {
        this.values = values;
    }

    private Object getValue(String key) {
        return values.get(key);
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toJSON() {
        Document d = DigitalAvatar.getDA().getDocNM(uid);
        return d == null ? null: d.toJSON();
    }

    public void remove() {
        DigitalAvatar.getDA().deleteDoc(getUid());
    }

    public void removeValue(String name) {
        values.remove(name);
        remove();
        create(this);
    }

    public AbstractEntity get(String name){
        AbstractEntity result = null;
        Value aux = (Value) getValue(name);
        if(aux.getType().equals("entity")){
            Query query = QueryBuilder
                    .select(SelectResult.all())
                    .from(DigitalAvatar.getDataSource())
                    .where(Expression.property("type").equalTo(Expression.string("entity"))
                            .and(Meta.id).equalTo(Expression.string((String)aux.get())));
            try {
                ResultSet rs = query.execute();
                Dictionary dic = rs.next().getDictionary(0);
                if(dic != null) {
                    result = new Entity(dic.getString("uid"),dic.getString("name"),dic.getString("type"),
                            dic.getArray("privacy").toList().toArray(new String[]{}),dic.getDate("timestamp"),null);
                    Array a = dic.getArray("value");
                    Map<String,Value> valuesMap = new TreeMap<>();
                    for(int i=0; i<a.count();i++){
                        Dictionary d = a.getDictionary(i);
                        Value v = new Value(
                                d.getString("name"),d.getString("type"),d.getArray("privacy").toList().toArray(new String[]{}),
                                d.getDate("timestamp"),null);
                        if(v.getType().equals("String") || v.getType().equals("entity")){
                            v.set(d.getString("value"));
                        } else if(v.getType().equals("int")){
                            v.set(d.getInt("value"));
                        } else if(v.getType().equals("double")){
                            v.set(d.getDouble("value"));
                        }
                        valuesMap.put(d.getString("name"), v);
                    }
                    ((Entity) result).setValues(valuesMap);
                }
            } catch (CouchbaseLiteException e) {
                Log.e("CouchbaseError", e.getLocalizedMessage());
            }
        } else{
            result = aux;
        }
        return result;
    }

    public List<AbstractEntity> getAll(){
        List<AbstractEntity> result = new ArrayList<>();
        result.addAll(values.values());
        return result;
    }

    public void set(String name, AbstractEntity value){
        add(name, value);
    }

    public void add(String name, AbstractEntity value){
        values.put(name,(Value) value);
        create(this);
    }

    public static String create(Entity e){
        MutableDocument eDoc;
        if(e.getUid() == null) {
            eDoc= new MutableDocument();
        } else{
            eDoc = new MutableDocument(e.getUid());
        }
        eDoc.setString("uid", eDoc.getId());
        eDoc.setString("name", e.getName());
        eDoc.setString("type", "entity");
        MutableArray privacy = new MutableArray();
        for(int i=0;i<e.getPrivacy().length;i++){
            privacy.addString(e.getPrivacy()[i]);
        }
        eDoc.setArray("privacy", privacy);
        eDoc.setDate("timestamp", e.getTimestamp());

        MutableArray values = new MutableArray();
        for(String key : e.getValues().keySet()){
            MutableDictionary value = new MutableDictionary();
            Value v = (Value)e.getValue(key);
            if(v.getType().equals("entity")){
                value.setString("type", "entity");
                value.setString("value", (String)v.get());
            } else if(v.getType().equals("int")){
                value.setString("type", "int");
                value.setInt("value", (int)v.get());
            } else if(v.getType().equals("double")){
                value.setString("type", "double");
                value.setDouble("value", (double)v.get());
            } else if(v.getType().equals("String")){
                value.setString("type", "String");
                value.setString("value", (String)v.get());
            }

            value.setString("name", key);
            value.setDate("timestamp", v.getTimestamp());
            MutableArray pr = new MutableArray();
            for(int i=0;i<v.getPrivacy().length;i++){
                pr.addString(v.getPrivacy()[i]);
            }
            value.setArray("privacy", pr);
            values.addDictionary(value);
        }
        eDoc.setArray("value",values);
        Log.i("Digital Avatars", "creando entity... "+ eDoc.getString("name"));
        DigitalAvatar.getDA().saveDoc(eDoc);
        return eDoc.getId();
    }
}
