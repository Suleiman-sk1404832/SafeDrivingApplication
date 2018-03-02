package qa.edu.qu.cmps312.safedrivingapplication.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Car implements Parcelable {
    public static final Parcelable.Creator<Car> CREATOR = new Parcelable.Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel source) {
            return new Car(source);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };
    private String make;
    private String model;
    private String year;
    private int milage;

    public Car(String make, String model, String year, int milage) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.milage = milage;
    }

    public Car() {
    }

    protected Car(Parcel in) {
        this.make = in.readString();
        this.model = in.readString();
        this.year = in.readString();
        this.milage = in.readInt();
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getMilage() {
        return milage;
    }

    public void setMilage(int milage) {
        this.milage = milage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.make);
        dest.writeString(this.model);
        dest.writeString(this.year);
        dest.writeInt(this.milage);
    }
}
