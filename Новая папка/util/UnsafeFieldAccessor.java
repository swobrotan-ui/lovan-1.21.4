package util;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import sun.misc.Unsafe;

public class UnsafeFieldAccessor<T> {
   private final Object instance;
   private final Class<?> localClass;
   private final Field localField;

   public UnsafeFieldAccessor(Object object, Class<?> oclass, Field field) {
      this.instance = object;
      this.localClass = oclass;
      this.localField = field;
   }

   public UnsafeFieldAccessor(Object object, Class<?> oclass, int i) {
      this(object, oclass, oclass.getDeclaredFields()[i]);
   }

   public UnsafeFieldAccessor(Object object, Class<?> oclass, Class<?> oclass1, int i) {
      this(object, oclass, getFromClazz(oclass, oclass1, i));
   }

   public UnsafeFieldAccessor(Object object, Class<?> oclass, Class<?> oclass1) {
      this(object, oclass, oclass1, 0);
   }

   public UnsafeFieldAccessor(Object object, Class<?> oclass, String s) {
      this(object, oclass, getFromName(oclass, s));
   }

   public T getValue() {
      return this.getValue(this.instance);
   }

   public T getValue(Object object) {
      try {
         if (object == null) {
            this.localField.setAccessible(true);
            return (T)this.localField.get(null);
         } else {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe)field.get(null);
            long i = unsafe.objectFieldOffset(this.localField);
            return (T)unsafe.getObject(object, i);
         }
      } catch (Exception exception) {
         return null;
      }
   }

   public long getLong(Object object) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            return unsafe.getLong(object1, j);
         } else {
            return unsafe.getLong(object, i);
         }
      } catch (Exception exception) {
         return 0L;
      }
   }

   public boolean getBoolean(Object object) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            return unsafe.getBoolean(object1, j);
         } else {
            return unsafe.getBoolean(object, i);
         }
      } catch (Exception exception) {
         return false;
      }
   }

   public int getInt(Object object) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            return unsafe.getInt(object1, j);
         } else {
            return unsafe.getInt(object, i);
         }
      } catch (Exception exception) {
         return 0;
      }
   }

   public float getFloat(Object object) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            return unsafe.getFloat(object1, j);
         } else {
            return unsafe.getFloat(object, i);
         }
      } catch (Exception exception) {
         return 0.0F;
      }
   }

   public long getLong() {
      return this.getLong(this.instance);
   }

   public int getInt() {
      return this.getInt(this.instance);
   }

   public boolean getBoolean() {
      return this.getBoolean(this.instance);
   }

   public float getFloat() {
      return this.getFloat(this.instance);
   }

   public void check(T object) {
      if (this.getValue() != object) {
         this.setValue((T)object);
      }
   }

   public void setValue(Object object, T object1) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object2 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            unsafe.putObject(object2, j, object1);
         } else {
            unsafe.putObject(object, i, object1);
         }
      } catch (Exception exception) {
      }
   }

   public void setFloat(Object object, Float f) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            unsafe.putFloat(object1, j, f);
         } else {
            unsafe.putFloat(object, i, f);
         }
      } catch (Exception exception) {
      }
   }

   public void setDouble(Object object, double d0) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            unsafe.putDouble(object1, j, d0);
         } else {
            unsafe.putDouble(object, i, d0);
         }
      } catch (Exception exception) {
      }
   }

   public void setInt(Object object, int i) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long j = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long k = unsafe.staticFieldOffset(this.localField);
            unsafe.putInt(object1, k, i);
         } else {
            unsafe.putInt(object, j, i);
         }
      } catch (Exception exception) {
      }
   }

   public void setBoolean(Object object, boolean flag) {
      try {
         Field field = Unsafe.class.getDeclaredField("theUnsafe");
         field.setAccessible(true);
         Unsafe unsafe = (Unsafe)field.get(null);
         long i = unsafe.objectFieldOffset(this.localField);
         if (object == null) {
            Object object1 = unsafe.staticFieldBase(this.localField);
            long j = unsafe.staticFieldOffset(this.localField);
            unsafe.putBoolean(object1, j, flag);
         } else {
            unsafe.putBoolean(object, i, flag);
         }
      } catch (Exception exception) {
      }
   }

   public void setDouble(double d0) {
      this.setDouble(this.instance, d0);
   }

   public void setFloat(Float f) {
      this.setFloat(this.instance, f);
   }

   public void setInt(int i) {
      this.setInt(this.instance, i);
   }

   public void setBoolean(boolean flag) {
      this.setBoolean(this.instance, flag);
   }

   public void setValue(T object) {
      this.setValue(this.instance, (T)object);
   }

   public void copyFrom(T object) {
      try {
         this.removeModifier();
         this.setValue(this.instance, this.getValue(object));
      } catch (Exception exception) {
      }
   }

   public UnsafeFieldAccessor<T> removeModifier() {
      try {
         this.localField.setAccessible(true);
         Field field = Field.class.getDeclaredField("modifiers");
         field.setAccessible(true);
         int i = this.localField.getModifiers();
         i &= -17;
         i &= -3;
         field.setInt(this.localField, i);
      } catch (Exception exception) {
      }

      return this;
   }

   public Class<?> getLocalClass() {
      return this.localClass;
   }

   public Field getLocalField() {
      return this.localField;
   }

   private static Field getFromClazz(Class<?> oclass, Class<?> oclass1, int i) {
      Object object = null;

      for (Field field : oclass.getDeclaredFields()) {
         if (field.getType().equals(oclass1)) {
            if (i == 0) {
               return field;
            }

            i--;
         }
      }

      return (Field)object;
   }

   private static Field getFromName(Class<?> oclass, String s) {
      Field field = null;

      try {
         field = oclass.getDeclaredField(s);
      } catch (Exception exception) {
         System.out.println("Error field " + s);
      }

      return field;
   }

   public static Field getFieldFromOffset(Class<?> oclass, Field field, int i) {
      int j = Arrays.<Field>asList(oclass.getDeclaredFields()).indexOf(field) + i;
      return oclass.getDeclaredFields()[j];
   }

   public static int getFieldIndex(Class<?> oclass, String s) {
      Field[] afield = oclass.getDeclaredFields();

      for (int i = 0; i < afield.length; i++) {
         if (afield[i].getName().equals(s)) {
            return i;
         }
      }

      return -1;
   }

   public static void printAllFieldIndexes(Class<?> oclass) {
      Field[] afield = oclass.getDeclaredFields();
      PrintStream printstream = System.out;
      String s = oclass.getSimpleName();
      printstream.println("Fields in " + s + ":");

      for (int i = 0; i < afield.length; i++) {
         printstream = System.out;
         String s1 = afield[i].getType().getSimpleName();
         String s2 = afield[i].getName();
         printstream.println("Index " + i + ": " + s2 + " (" + s1 + ")");
      }
   }
}
