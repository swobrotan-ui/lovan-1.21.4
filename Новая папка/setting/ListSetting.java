package setting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListSetting extends Setting {
   @Expose
   private List<String> selectedValues;
   @Expose
   private final List<String> defaultValues;
   @Expose
   private List<String> availableValues;
   @Expose
   private final boolean allowMultiple;
   @Expose
   private int maxSelections;
   @Expose
   private final int minSelections;
   private transient Runnable onSelectionChange;

   public ListSetting(String s, String s1, List<String> list, List<String> list1) {
      this(s, s1, list, list1, true, list.size(), 0);
   }

   public ListSetting(String s, String s1, List<String> list, List<String> list1, boolean flag) {
      this(s, s1, list, list1, flag, flag ? list.size() : 1, 0);
   }

   public ListSetting(String s, String s1, List<String> list, List<String> list1, boolean flag, int i, int j) {
      super(s, s1);
      if (list != null && !list.isEmpty()) {
         this.availableValues = new ArrayList<String>(list);
         this.defaultValues = new ArrayList<String>((Collection<? extends String>)(list1 != null ? list1 : new ArrayList()));
         this.selectedValues = new ArrayList<String>(this.defaultValues);
         this.allowMultiple = flag;
         this.maxSelections = Math.max(1, i);
         this.minSelections = Math.max(0, j);
         this.n();
         this.o();
      } else {
         throw new IllegalArgumentException("Available values cannot be null or empty");
      }
   }

   public void setSelectedValues(List<String> list) {
      ArrayList arraylist = new ArrayList<String>(this.selectedValues);
      if (list == null) {
         this.selectedValues = new ArrayList<String>();
      } else {
         this.selectedValues = new ArrayList<String>(list);
      }

      this.o();
      if (!arraylist.equals(this.selectedValues)) {
         this.notifyChange();
         this.e();
      }
   }

   public void select(String s) {
      if (s != null && this.availableValues.contains(s)) {
         boolean flag = false;
         if (!this.allowMultiple) {
            if (!this.selectedValues.isEmpty() && !this.selectedValues.getFirst().equals(s)) {
               this.selectedValues.clear();
               this.selectedValues.add(s);
               flag = true;
            } else if (this.selectedValues.isEmpty()) {
               this.selectedValues.add(s);
               flag = true;
            }
         } else if (!this.selectedValues.contains(s) && this.selectedValues.size() < this.maxSelections) {
            this.selectedValues.add(s);
            flag = true;
         }

         if (flag) {
            this.notifyChange();
            this.e();
         }
      }
   }

   public void deselect(String s) {
      if (this.selectedValues.size() > this.minSelections && this.selectedValues.remove(s)) {
         this.notifyChange();
         this.e();
      }
   }

   public void toggle(String s) {
      if (this.selectedValues.contains(s)) {
         this.deselect(s);
      } else {
         this.select(s);
      }
   }

   @Override
   public void fromJson(JsonObject jsonobject) {
      boolean flag = false;
      if (jsonobject.has("selectedValues")) {
         JsonArray jsonarray = jsonobject.getAsJsonArray("selectedValues");
         ArrayList arraylist = new ArrayList();

         for (int i = 0; i < jsonarray.size(); i++) {
            String s = jsonarray.get(i).getAsString();
            if (this.availableValues.contains(s)) {
               arraylist.add(s);
            }
         }

         ArrayList arraylist1 = new ArrayList<String>(this.selectedValues);
         this.selectedValues = new ArrayList<String>(arraylist);
         this.o();
         if (!arraylist1.equals(this.selectedValues)) {
            flag = true;
         }
      }

      if (jsonobject.has("visible")) {
         this.visible = jsonobject.get("visible").getAsBoolean();
      }

      if (flag) {
         this.notifyChange();
         this.e();
      }
   }

   private void e() {
      if (this.onSelectionChange != null) {
         try {
            this.onSelectionChange.run();
            return;
         } catch (Exception exception) {
         }
      }
   }

   public boolean isSelected(String s) {
      return this.selectedValues.contains(s);
   }

   public String getFirst() {
      return this.selectedValues.isEmpty() ? null : this.selectedValues.getFirst();
   }

   public Set<String> getSelectedSet() {
      return new HashSet<String>(this.selectedValues);
   }

   public void deselectAll() {
      if (this.minSelections == 0 && !this.selectedValues.isEmpty()) {
         this.selectedValues.clear();
         this.notifyChange();
         this.e();
      }
   }

   public void selectAll() {
      if (this.allowMultiple) {
         ArrayList arraylist = new ArrayList<String>(this.selectedValues);
         this.selectedValues.clear();
         int i = Math.min(this.availableValues.size(), this.maxSelections);
         this.selectedValues.addAll(this.availableValues.subList(0, i));
         if (!arraylist.equals(this.selectedValues)) {
            this.notifyChange();
            this.e();
         }
      }
   }

   @Override
   public void reset() {
      this.setSelectedValues(new ArrayList<String>(this.defaultValues));
   }

   public void setAvailableValues(List<String> list) {
      if (list != null && !list.isEmpty()) {
         ArrayList arraylist = new ArrayList<String>(this.selectedValues);
         this.availableValues = new ArrayList<String>(list);
         this.maxSelections = this.allowMultiple ? list.size() : 1;
         this.selectedValues = new ArrayList<String>();

         for (String s : arraylist) {
            if (this.availableValues.contains(s)) {
               this.selectedValues.add(s);
            }
         }

         if (this.selectedValues.isEmpty() && !this.availableValues.isEmpty()) {
            this.selectedValues.add(this.availableValues.getFirst());
         }

         this.o();
         this.notifyChange();
         this.e();
      }
   }

   private void n() {
      if (this.minSelections > this.maxSelections) {
         throw new IllegalArgumentException("minSelections cannot be greater than maxSelections");
      } else if (this.maxSelections > this.availableValues.size()) {
         throw new IllegalArgumentException("maxSelections cannot be greater than available values count");
      } else if (!this.allowMultiple && this.maxSelections > 1) {
         throw new IllegalArgumentException("maxSelections cannot be greater than 1 when allowMultiple is false");
      }
   }

   private void o() {
      this.selectedValues.removeIf(s2 -> {
         return !this.availableValues.contains(s2);
      });

      while (this.selectedValues.size() < this.minSelections && this.selectedValues.size() < this.availableValues.size()) {
         for (String s : this.availableValues) {
            if (!this.selectedValues.contains(s)) {
               this.selectedValues.add(s);
               break;
            }
         }
      }

      while (this.selectedValues.size() > this.maxSelections) {
         this.selectedValues.removeLast();
      }

      if (!this.allowMultiple && this.selectedValues.size() > 1) {
         String s1 = this.selectedValues.getFirst();
         this.selectedValues.clear();
         this.selectedValues.add(s1);
      }
   }

   public boolean isAvailableValue(String s) {
      return this.selectedValues.contains(s);
   }

   public boolean isAtDefault() {
      return this.selectedValues.equals(this.defaultValues);
   }

   public int getSelectedCount() {
      return this.selectedValues.size();
   }

   public boolean isMaxReached() {
      return this.selectedValues.size() >= this.maxSelections;
   }

   public boolean isMinReached() {
      return this.selectedValues.size() <= this.minSelections;
   }

   public String getDisplayText() {
      if (this.selectedValues.isEmpty()) {
         String s = this.name;
         return s + ": None";
      } else if (this.selectedValues.size() == 1) {
         String s4 = this.name;
         String s1 = this.selectedValues.getFirst();
         String s2 = s4;
         return s2 + ": " + s1;
      } else {
         String s5 = this.name;
         int i = this.selectedValues.size();
         String s3 = s5;
         return s3 + ": " + i + " selected";
      }
   }

   public String getFormattedValue() {
      if (this.selectedValues.isEmpty()) {
         return "None";
      } else {
         return this.selectedValues.size() == 1 ? this.selectedValues.getFirst() : String.join(", ", this.selectedValues);
      }
   }

   @Override
   public JsonObject toJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("name", this.name);
      jsonobject.addProperty("description", this.description);
      jsonobject.addProperty("type", this.getType());
      jsonobject.addProperty("allowMultiple", this.allowMultiple);
      jsonobject.addProperty("maxSelections", this.maxSelections);
      jsonobject.addProperty("minSelections", this.minSelections);
      jsonobject.addProperty("selectedCount", this.getSelectedCount());
      jsonobject.addProperty("isMaxReached", this.isMaxReached());
      jsonobject.addProperty("isMinReached", this.isMinReached());
      jsonobject.addProperty("isAtDefault", this.isAtDefault());
      jsonobject.addProperty("displayText", this.getDisplayText());
      jsonobject.addProperty("formattedValue", this.getFormattedValue());
      jsonobject.addProperty("visible", this.visible);
      JsonArray jsonarray = new JsonArray();

      for (String s : this.selectedValues) {
         jsonarray.add(s);
      }

      jsonobject.add("selectedValues", jsonarray);
      JsonArray jsonarray1 = new JsonArray();

      for (String s1 : this.defaultValues) {
         jsonarray1.add(s1);
      }

      jsonobject.add("defaultValues", jsonarray1);
      JsonArray jsonarray2 = new JsonArray();

      for (String s2 : this.availableValues) {
         jsonarray2.add(s2);
      }

      jsonobject.add("availableValues", jsonarray2);
      return jsonobject;
   }

   @Override
   public String getType() {
      return "list";
   }

   public List<String> getSelectedValues() {
      return this.selectedValues;
   }

   public List<String> getDefaultValues() {
      return this.defaultValues;
   }

   public List<String> getAvailableValues() {
      return this.availableValues;
   }

   public boolean isAllowMultiple() {
      return this.allowMultiple;
   }

   public int getMaxSelections() {
      return this.maxSelections;
   }

   public int getMinSelections() {
      return this.minSelections;
   }

   public Runnable getOnSelectionChange() {
      return this.onSelectionChange;
   }

   public void setAvailableValuesRaw(List<String> list) {
      this.availableValues = list;
   }

   public void setMaxSelections(int i) {
      this.maxSelections = i;
   }

   public void setOnSelectionChange(Runnable runnable) {
      this.onSelectionChange = runnable;
   }
}
