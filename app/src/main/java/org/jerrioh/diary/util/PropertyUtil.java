package org.jerrioh.diary.util;

import org.jerrioh.diary.model.Property;
import org.jerrioh.diary.model.db.PropertyDao;

public class PropertyUtil {

    public static String getProperty(Property.Key propertyKey, PropertyDao propertyDao) {
        String propertyValue = propertyDao.getPropertyValue(propertyKey);
        if (propertyValue == null) {
            propertyDao.insertProperty(propertyKey, propertyKey.DEFAULT_VALUE);
            propertyValue = propertyKey.DEFAULT_VALUE;
        }
        return propertyValue;
    }

    public static void setProperty(Property.Key propertyKey, String newValue, PropertyDao propertyDao) {
        String propertyValue = propertyDao.getPropertyValue(propertyKey);
        if (propertyValue == null) {
            propertyDao.insertProperty(propertyKey, newValue);
        } else {
            propertyDao.updateProperty(propertyKey, newValue);
        }
    }
}
