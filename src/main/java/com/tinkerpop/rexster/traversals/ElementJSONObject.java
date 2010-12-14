package com.tinkerpop.rexster.traversals;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.rexster.Tokens;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class ElementJSONObject extends JSONObject {

    private final Object id;

    public ElementJSONObject(final Element element) throws JSONException {
        this(element, null);
    }

    public ElementJSONObject(final Element element, boolean showTypes) throws JSONException {
        this(element, null, showTypes);
    }

    public ElementJSONObject(final Element element, final List<String> propertyKeys) throws JSONException {
        this(element, propertyKeys, false);
    }

    public ElementJSONObject(final Element element, final List<String> propertyKeys, boolean showTypes) throws JSONException {
        this.id = element.getId();
        if (element instanceof Vertex) {
            this.put(Tokens._TYPE, Tokens.VERTEX);
        } else {
            this.put(Tokens._TYPE, Tokens.EDGE);
        }

        if (propertyKeys == null) {
            this.put(Tokens._ID, this.getValue(this.id, showTypes));
            for (String key : element.getPropertyKeys()) {
                this.put(key, this.getValue(element.getProperty(key), showTypes));
            }

            if (element instanceof Edge) {
                Edge edge = (Edge) element;
                this.put(Tokens._LABEL, edge.getLabel());
                this.put(Tokens._IN_V, this.getValue(edge.getInVertex().getId(), showTypes));
                this.put(Tokens._OUT_V, this.getValue(edge.getOutVertex().getId(), showTypes));
            }
        } else {
            for (String key : propertyKeys) {
                if (key.equals(Tokens._ID)) {
                    this.put(Tokens._ID, this.getValue(this.id, showTypes));
                } else if (element instanceof Edge && key.equals(Tokens._LABEL)) {
                    Edge edge = (Edge) element;
                    this.put(Tokens._LABEL, edge.getLabel());
                } else if (element instanceof Edge && key.equals(Tokens._IN_V)) {
                    Edge edge = (Edge) element;
                    this.put(Tokens._IN_V, this.getValue(edge.getInVertex().getId(), showTypes));
                } else if (element instanceof Edge && key.equals(Tokens._OUT_V)) {
                    Edge edge = (Edge) element;
                    this.put(Tokens._IN_V, this.getValue(edge.getOutVertex().getId(), showTypes));
                } else {
                    Object temp = this.getValue(element.getProperty(key), showTypes);
                    if (null != temp) {
                        this.put(key, temp);
                    }
                }
            }
        }
    }

    private Object getValue(Object value, boolean includeType) throws JSONException {

        // type will be one of: map, list, string, long, int, double, float.
        // in the event of a complex object it will call a toString and store as a
        // string
        String type = determineType(value);

        // if the includeType is set to true then show the data types of the properties
        if (includeType) {
            JSONObject valueAndType = new JSONObject();
            valueAndType.put("type", type);

            if (type.equals("list")) {

                // values of lists must be accumulated as JSONObjects under the value key.
                // will return as a JSONArray. called recursively to traverse the entire
                // object graph of each item in the array.
                List list = (List) value;
                for (Object o : list) {
                    valueAndType.accumulate("value", getValue(o, includeType));
                }
            } else if (type.equals("map")) {

                // maps are converted to a JSONObject.  called recursively to traverse
                // the entire object graph within the map.
                JSONObject convertedMap = new JSONObject();
                Map map = (Map) value;
                Set keys = map.keySet();
                Iterator keyIterator = keys.iterator();
                while (keyIterator.hasNext()) {
                    Object key = keyIterator.next();
                    convertedMap.put(key.toString(), getValue(map.get(key), includeType));
                }

                valueAndType.put("value", convertedMap);
            } else {

                // this must be a primitive value or a complex object.  if a complex
                // object it will be handled by a call to toString and stored as a
                // string value
                valueAndType.put("value", value);
            }

            // this goes back as a JSONObject with data type and value
            return valueAndType;
        } else {

            // even though the data type is not needed here, jettison needs these
            // objects converted to objects they understand.
            if (type.equals("list")) {
                List list = (List) value;
                JSONArray jsonArray = new JSONArray(list);
                return jsonArray;
            } else if (type.equals("map")) {
                JSONObject jsonObject = new JSONObject((Map) value);
                return jsonObject;
            } else {
                return value;
            }
        }
    }

    private String determineType(Object value) {
        String type = "string";
        if (value instanceof Double) {
            type = "double";
        } else if (value instanceof Float) {
            type = "float";
        } else if (value instanceof Integer) {
            type = "integer";
        } else if (value instanceof Long) {
            type = "long";
        } else if (value instanceof List) {
            type = "list";
        } else if (value instanceof Map) {
            type = "map";
        }

        return type;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public Object getId() {
        return this.id;
    }

    public boolean equals(Object object) {
        if (object instanceof ElementJSONObject)
            return ((ElementJSONObject) object).getId().equals(this.id);
        else
            return false;
    }
}

