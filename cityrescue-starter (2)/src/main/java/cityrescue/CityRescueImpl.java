package cityrescue;

import cityrescue.enums.*;
import cityrescue.exceptions.*;

/**
 * CityRescueImpl (Starter)
 *
 * Your task is to implement the full specification.
 * You may add additional classes in any package(s) you like.
 */
public class CityRescueImpl implements CityRescue {
    // TODO: add fields (map, arrays for stations/units/incidents, counters, tick, etc.)

    // Fields that store the x(width) and y(length)
    // dimensions of the city grid
    private int width;
    private int height;

    // tracks the stimulated steps of the
    // emergency services
    private int tick;

    // 2D array so we can track obstacles
    // and their positions
    private boolean[][] obstacles;

    // Storage Limits as stated
    private static final int MAX_STATIONS = 20;


    @Override
    public void initialise(int width, int height) throws InvalidGridException {
        // Ensures that the width and height are positive
        if (width <= 0 || height <= 0) {
            // If not the message below is printed
            throw new InvalidGridException("The width: " + width + "and the height: " + height + ".You have inputted are invalid");
        }

        // Stores the valid inputted dimensions
        this.width = width;
        this.height = height;

        // resets all the stimulation states
        this.tick = 0;
        // throw new UnsupportedOperationException("Not implemented yet");

        // Intializes the grid with the width and height the user has inputted
        // making obstacle grid all false where
        // FALSE REPRESENTS AN EMPTY SPACE IN THE GRID
        obstacles = new boolean[width][height];

        // Resets all the states
        stationCount = 0;
        unitCount = 0;
        stations = new Station[MAX_STATIONS];
        units = new Unit[MAX_UNITS];
        
        // Resets static ID generators
        Station.nextStationID = 1;
        Unit.nextUnitID = 1;
    }
    

    @Override
    public int[] getGridSize() {
        // TODO: implement
        // returns the valid stored width and height;
        return new int[]{width, height};
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        // TODO: implement
        // Checks if the obstacles coords are within the city grid
        if (!InBounds(x, y)) {
            // If not the message
            throw new InvalidLocationException("Your position for the obstacle at (" + x + ", " + y + ") is invalid!!");
        }
        // throw new UnsupportedOperationException("Not implemented yet");

        // The obstacle has now been placed and is registered as TRUE
        obstacles[x][y] = true;
    }

    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {
        // TODO: implement
        if (!InBounds(x, y)) {
            throw new InvalidLocationException("Your position for the obstacle at (" + x + ", " + y + ") is invalid!!");
        }
        // throw new UnsupportedOperationException("Not implemented yet");

        // Now the cell in the array is unblocked and is set back to FALSE
        obstacles[x][y] = false;
    }

    // Checks if the users input (x, y) for an obstacle is valid
    private boolean InBounds(int x, int y) {
        // Returns true if x and y are within the height
        // and width of the initalized grid and also
        // returns false if the coordinate is not valid
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    // Creates an array of stations to hold a max of 50 stations
    // for safe measure
    private Station[] stations = new Station[MAX_STATIONS];
    private int stationCount = 0;

    private static class Station {
        // Unique ID for the station
        int id;
        // x and y position on the 2d array of the grid
        int x;
        int y;
        // Name of the station
        String name;
        // maximum capacity of the station
        int maxUnits;
        int unitCount = 0;

        // Static counter that gives each station a unique ID
        private static int nextStationID = 1;

        Station(int x, int y, String name, int maxUnits) {
            // The next station after would always be 1 larger
            // than the prior station
            // getters
            this.id = nextStationID++;
            this.x = x;
            this.y = y;
            this.name = name;
            this.maxUnits = maxUnits;

        }
    }


    @Override
    public int addStation(String name, int x, int y) throws InvalidNameException, InvalidLocationException {
        // TODO: implement
        if (name == null || name.trim().isEmpty()) {
            // Ensures that an empty name or a null name produces below
            throw new InvalidNameException("The name you have inputted is invalid");
        }
        if (!InBounds(x, y) || obstacles[x][y]) {
            // Ensures that the position for the station is on the grid
            // and is not on a position where an obstacle has been added
            throw new InvalidLocationException("The location you have put (" + x + ", " + y + ") is invalid!!");
        }
        
        // Ensures that the Station count never exceeds the maximum number of stations
        if (stationCount >= MAX_STATIONS)
            throw new CapacityExceededException("Maximum stations reached");

        // New station object is now made if the above is passed
        // where the maxUnits is a default 1
        Station station = new Station(x, y, name, 1);
        // add station to the array and increment station count
        stations[stationCount++] = station;

        // return the valid station id to the unique station
        return station.id;

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        // index that tracks the position of al current stations in the array
        int index = -1;
        // loop through all the stations to identify the one that matches the id
        for (int i = 0; i < stationCount; i++) {
            if (stations[i].id == stationId) {
                // checks if the station jas any units
                if (stations[i].unitCount > 0) {
                    throw new IllegalStateException("Station : " + stationId + " still has units and therefore cannot be removed!");
                }
                // stores the index of the station we can rightfully remove
                index = i;
                break;
            }
        }
        // when the station is not found then the throw statement
        if (index == -1) {
            throw new IDNotRecognisedException("StationID : " + stationId + " is not valid.");
        }

        // Deterministic removal 
        // Removing the station from the array and shofting all remaining 
        // stationd down by one
        for (int i = index; i < stationCount - 1; i++) {
            stations[i] = stations[i + 1];
        }

        // removes the station that we can remove by swapping it with the last station on the array
        stations[index] = stations[stationCount - 1];
        // ignores the last decrement on the station count
        stations[stationCount - 1] = null;
        stationCount--;
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        // TODO: implement
        // loops through all the station IDs till we get a match
        for (int i = 0; i < stationCount; i++) {
            if (stations[i].id == stationId) {
                // Ensure the max capacity is negative
                if (maxUnits <= 0) {
                    throw new InvalidCapacityException("Invalid capacity of " + maxUnits + " for station ID " + stationId);
                }
                // Cannot reduce the maxUnits to be less than the current unit count
                if (maxUnits < stations[i].unitCount) {
                    throw new InvalidCapacityException(
                            "Cannot set capacity below current unit count (" + stations[i].unitCount + ")."
                    );
                }
                // sets the new station capacity
                stations[i].maxUnits = maxUnits;
                return;
            }
        }
        // if the station ID is not found
        throw new IDNotRecognisedException("StationID : " + stationId + " is not valid.");

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getStationIds() {
        // TODO: implement
        // new array that stores all the station ids
        int[] ids = new int[stationCount];
        // Array is filled with station IDs
        for (int i = 0; i < stationCount; i++) {
            ids[i] = stations[i].id;
        }
        // Simple bubble sort to sort IDs ascending
        for (int i = 0; i < ids.length - 1; i++) {
            for (int j = 0; j < ids.length - 1 - i; j++) {
                if (ids[j] > ids[j + 1]) {
                    // swaps IDs when not in ascending order
                    int temp = ids[j];
                    ids[j] = ids[j + 1];
                    ids[j + 1] = temp;
                }
            }
        
        }

        return ids;

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    // Maximum number of units
    private static final int MAX_UNITS = 50;
    // New array for the unit storage
    private Unit[] units = new Unit[MAX_UNITS];
    private int unitCount = 0;

    private static abstract class Unit {
        // Unit id
        int id;
        // Unit position x and y
        int x;
        int y;
        // What type of unit it is
        UnitType type;
        // What status the unit is
        UnitStatus status;
        // The ID of the station it comes from
        int homeStationID;
        // Assigned default incident ID
        int incidentId = -1;
        private static int nextUnitID = 1;

        // Constructor used by subclasses 
        Unit(int ogStationID, int x, int y, UnitType type){
            this.id = nextUnitID++;
            this.ogStationID = ogStationID;
            this.type = type;
            this.x = x;
            this.y = y;
            this.status = UnitStatus.IDLE;
        }

        // Determines if the unit can handle the given incident type
        abstract boolean canHandle(IncidentType incidentType);
        
        // ticks required to resolve the incident occuring
        abstract int getTicksToResolve(int severity);
    }

    // Subclass of Ambulance 
    private static class Ambulance extends Unit {
        Ambulance(int stationId, int x, int y) {
            super(stationId, x, y, UnitType.AMBULANCE);
        }
        // Handles the medical incidents
        @Override
        boolean canHandle(IncidentType incidentType) {
            return incidentType == IncidentType.MEDICAL;
        }
        // Fastest resolution time of 2 ticks
        @Override
        int getTicksToResolve(int severity) {
            return 2;
        }
    }
    
    // Subclass of fire engines
    private static class FireEngine extends Unit {
        FireEngine(int stationId, int x, int y) {
            super(stationId, x, y, UnitType.FIRE_ENGINE);
        }
        // Handles fire related incidents
        @Override
        boolean canHandle(IncidentType incidentType) {
            return incidentType == IncidentType.FIRE;
        }
        // Longest resolution time of 4 ticks
        @Override
        int getTicksToResolve(int severity) {
            return 4;
        }
    }
    // Subclass of Police cars
    private static class PoliceCar extends Unit {
        PoliceCar(int stationId, int x, int y) {
            super(stationId, x, y, UnitType.POLICE_CAR);
        }
        // Handles incidents of crime
        @Override
        boolean canHandle(IncidentType incidentType) {
            return incidentType == IncidentType.CRIME;
        }
        // Medium resolution time
        @Override
        int getTicksToResolve(int severity) {
            return 3;
        }
    }
    private Unit createUnit(UnitType type, Station station) throws InvalidUnitException {
        switch (type) {
            case AMBULANCE:
                return new Ambulance(station.id, station.x, station.y);
            case FIRE_ENGINE:
                return new FireEngine(station.id, station.x, station.y);
            case POLICE_CAR:
                return new PoliceCar(station.id, station.x, station.y);
            default:
                throw new InvalidUnitException("Unknown unit type");
            }
        }


    @Override
    public int addUnit(int stationId, UnitType type) throws IDNotRecognisedException, InvalidUnitException, IllegalStateException {
        // TODO: implement
        // Checks for a vaild unit type
        if(type == null){
            throw new InvalidUnitException("Unit type cannot be null");
        }
        // Checks if the maximum number of units on the grid has been reached
         if (unitCount >= MAX_UNITS){
            throw new CapacityExceededException("Maximum units reached");
        }

        // Finding a station to add a unit
        Station station = null;
        for (int i = 0; i < stationCount; i++){
            if(stations[i].id == stationId){
                station = stations[i];
                break;
            }
        }
        // Ensures that the station ID isn't null
        if(station == null){
            throw new IDNotRecognisedException("StationID : " + stationId + " is not valid.");
        }
        // Checks if the station has any space
        if(station.unitCount >= station.maxUnits){
            throw new IllegalStateException("The station: " + stationId + " is at its maximum capacity");
        }

       // Creates Subclass
       Unit unit = createUnit(type, station);
       
       units[unitCount++] = unit;
       station.unitCount++;
       return unit.id;
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        // Intialise index to -1, holding
        // the position of the unit in the units array
        int index = -1;

        // We find the unit by looping through all units
        for(int i = 0; i < unitCount; i++) {
            if(units[i].id == unitId){
                index = i;
                break;
            }
        }
        // If the unit was not found then..
        if(index == -1){
            throw new IDNotRecognisedException("Unit ID: " + unitId + " is not found");
        }
        // We are getting the unit object from the array
        Unit unit = units[index];

        // makes sure the unit is not occupied
        if(unit.status == UnitStatus.EN_ROUTE || unit.status == UnitStatus.AT_SCENE){
            throw new IllegalStateException("The unit is currently in use!");
        }
        // Removes the unit count from the original station
        for( int i = 0; i < stationCount; i++){
            if(stations[i].id == unit.homeStationID){
                stations[i].unitCount--;
            }
        }
        // Deterministic removal shifts all to the left
        for (int i = index; i < unitCount - 1; i++){
            units[i] = units[i + 1];
        }
        
        units[--unitCount] = null;
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        int index = -1;
        Unit unit = null;

        // We find the unit in the units array
        for(int i = 0; i < unitCount; i++) {
            if(units[i].id == unitId){
                // Assigns the unit here.
                unit = units[i];
                break;
            }
        }
        // When unit is not found...
        if(unit == null){
            throw new IDNotRecognisedException("The unit ID " + unitId + " is not found" );
        }
        // Only Idle vehicles can be transferred
        // so for everything other that IDLE the following...
        if(unit.status != UnitStatus.IDLE){
            throw new IllegalStateException("The unit is currently occupied!");
        }

        // finds the station to transfer a unit
        Station newStation = null;
        for (int i = 0; i < stationCount; i++) {
            if (stations[i].id == newStationId) {
                newStation = stations[i];
                break;
            }
        }
        // If the new station is not found...
       if(newStation == null){
           throw new IDNotRecognisedException("The station ID " + newStationId + " is not valid");
       }
       // If the new station is at maximum capacity
       if(newStation.unitCount >= newStation.maxUnits){
           throw new IllegalStateException("The station ID " + newStationId + " is it max capacity");
       }
       // Removes the registered vehicle from the old station
       for(int i = 0; i < stationCount; i++){
           if(stations[i].id == unit.ogStationID){
               stations[i].unitCount--;
           }
        }
       // Returns new stationID and it's position
       unit.ogStationID = newStationId;
       unit.x = newStation.x;
       unit.y = newStation.y;
       // The new unit is now an increment into the new station
       newStation.unitCount++;

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        // Finds the unit ID
        Unit unit = null;
        for (int i = 0; i < unitCount; i++) {
            if (units[i].id == unitId) {
                unit = units[i];
                break;
            }
        }
        // if unit not found then..
        if (unit == null) {
            throw new IDNotRecognisedException("Unit ID " + unitId + " not found.");
        }
        if (outOfService) {
            // Can only mark OUT_OF_SERVICE if IDLE
            if (unit.status != UnitStatus.IDLE) {
                throw new IllegalStateException("Unit " + unitId + " is not IDLE and cannot go out of service.");
            }
            // Marks the unit as out of service
            unit.status = UnitStatus.OUT_OF_SERVICE;
        } 
        // Returning to service makes unit IDLE 
        else if (unit.status == UnitStatus.OUT_OF_SERVICE) {
                unit.status = UnitStatus.IDLE;
            }
        }
    
        // throw new UnsupportedOperationException("Not implemented yet");


    @Override
    public int[] getUnitIds() {
        // TODO: implement
        // New Array that will hold all active Unit IDs
        int[] ids = new int[unitCount];

        // Loops through all units and copies their IDs into the array
        for(int i = 0; i < unitCount; i++) {
            ids[i] = units[i].id;
        }

        // Sorts the IDs in ascending order for
        for (int i = 0; i < ids.length - 1; i++) {
            for (int j = 0; j < ids.length - 1 - i; j++) {
                if (ids[j] > ids[j + 1]) {
                    int temp = ids[j];
                    ids[j] = ids[j + 1];
                    ids[j + 1] = temp;
                }
            }
        }

        // Return the sorted array of unit IDs
        return ids;


        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewUnit(int unitId) throws IDNotRecognisedException {
        // TODO: implement

        Unit unit = null;
        for (int i = 0; i < unitCount; i++) if (units[i].id == unitId) unit = units[i];
        if (unit == null) throw new IDNotRecognisedException("Unit ID not found");

        return "U#" + unit.id +
                " TYPE=" + unit.type +
                " STATUS=" + unit.status +
                " LOC=(" + unit.x + "," + unit.y + ")" +
                " STATION=" + unit.ogStationID;
        }
        // throw new UnsupportedOperationException("Not implemented yet");
    

    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getIncidentIds() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void dispatch() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void tick() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String getStatus() {
        // TODO: implement
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
