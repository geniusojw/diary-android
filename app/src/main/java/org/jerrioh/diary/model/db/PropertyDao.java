package org.jerrioh.diary.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.jerrioh.diary.model.Property;

import java.util.ArrayList;
import java.util.List;

public class PropertyDao extends AbstractDao {
    private static final String TAG = "PropertyDao";

    private static final String TABLE_NAME = Property.TableDesc.TABLE_NAME;
    private static final String[] COLUMN_NAMES = {
            Property.TableDesc.COLUMN_NAME_PROPERTY_KEY,
            Property.TableDesc.COLUMN_NAME_PROPERTY_VALUE
    };

    public PropertyDao(Context context) {
        super(context);
    }

    public String getPropertyValue(Property.Key propertyKey) {
        String selection = Property.TableDesc.COLUMN_NAME_PROPERTY_KEY + "=?";
        String[] args = { propertyKey.KEY };

        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        if (cursorHasJustOne(cursor)) {
            cursor.moveToFirst();
            return cursor.getString(1);
        } else {
            return null;
        }
    }

    public List<Property> getAllProperties() {
        String selection = "1 = 1";
        String[] args = { };
        Cursor cursor = readableDb().query(TABLE_NAME, COLUMN_NAMES, selection, args, null, null, null);
        List<Property> properties = new ArrayList<>();
        if (cursorIsNotNull(cursor)) {
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(0);
                    String value = cursor.getString(1);
                    properties.add(new Property(key, value));
                } while (cursor.moveToNext());
            }
        }
        return properties;
    }


    public long insertProperty(Property.Key propertyKey, String value) {
        Log.d(TAG, "insert setting KEY" + propertyKey.KEY + ", value" + value);
        ContentValues contentValues = new ContentValues();
        contentValues.put(Property.TableDesc.COLUMN_NAME_PROPERTY_KEY, propertyKey.KEY);
        contentValues.put(Property.TableDesc.COLUMN_NAME_PROPERTY_VALUE, value);
        return writableDb().insert(TABLE_NAME, null, contentValues);
    }

    public long updateProperty(Property.Key propertyKey, String value) {
        Log.d(TAG, "update setting KEY" + propertyKey.KEY + ", value" + value);

        String selection = Property.TableDesc.COLUMN_NAME_PROPERTY_KEY + "=?";
        String[] args = { propertyKey.KEY };

        ContentValues contentValues = new ContentValues();
        contentValues.put(Property.TableDesc.COLUMN_NAME_PROPERTY_VALUE, value);

        return writableDb().update(TABLE_NAME, contentValues, selection, args);
    }

    public long deleteProperty(Property.Key propertyKey) {
        String selection = Property.TableDesc.COLUMN_NAME_PROPERTY_KEY + "=?";
        String[] args = { propertyKey.KEY };

        return writableDb().delete(TABLE_NAME, selection, args);
    }

    public long deleteAllProperties() {
        return writableDb().delete(TABLE_NAME, "1=1", new String[]{});
    }
}
