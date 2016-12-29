package com.vancouvertraffic;

/**
 * Created by Danny on 28/12/2016.
 */

public class CamData {
    // private variables
    int _id;
    String Name = "name";
    String Latitude = "Latitude";
    String Longitude = "Longitude";
    String camnorth = "";
    String cameast = "";
    String camsouth = "";
    String camwest = "";

    // constructor.  empty data.
    public CamData(){

    }


    public void setID(int id)
    {
        this._id = id;
    }

    public void setName(String _name)
    {
        this.Name = _name;
    }

    public void setLatitude(String _Latitude)
    {
        this.Latitude = _Latitude;
    }

    public void setLongitude(String _Longitude)
    {
        this.Longitude = _Longitude;
    }

    public int getID()
    {
        return _id;
    }

    public String getName()
    {
        return Name;
    }

    public String getLatitude()
    {
        return Latitude;
    }

    public String getLongitude()
    {
        return Longitude;
    }

    public void setCamNorth(String _camnorth)
    {
        this.camnorth = _camnorth;
    }

    public String getCamNorth()
    {
        return camnorth;
    }

    public void setCamEast(String _cameast)
    {
        this.cameast = _cameast;
    }

    public String getCamEast()
    {
        return cameast;
    }

    public void setCamSouth(String _camsouth)
    {
        this.camsouth = _camsouth;
    }

    public String getCamSouth()
    {
        return camsouth;
    }

    public void setCamWest(String _camwest)
    {
        this.camwest = _camwest;
    }

    public String getCamWest()
    {
        return camwest;
    }
}
