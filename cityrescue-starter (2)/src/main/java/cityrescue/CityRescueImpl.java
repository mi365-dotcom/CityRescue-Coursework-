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
    // Encapsulates grid size, obstacles and legal moves
    private CityMap cityMap;

    private static class CityMap {
        private final int width;
        private final int height;
        private final boolean[][] obstacles;

        CityMap(int width, int height) {
            this.width = width;
            this.height = height;
            obstacles = new boolean[width][height];
        }

         // Checks if the users input (x, y) for an obstacle is valid
        private boolean isInBounds(int x, int y) {
            // Returns true if x and y are within the height
            // and width of the initalized grid and also
            // returns false if the coordinate is not valid
            return x >= 0 && x < width && y >= 0 && y < height;
        }


        boolean isBlocked(int x, int y) {
            return obstacles[x][y];
        }

        void addObstacle(int x, int y) {
            obstacles[x][y] = true;
        }

        void removeObstacle(int x, int y) {
            obstacles[x][y] = false;
        }

        int countObstacles() {
            int c = 0;
            for (int i = 0; i < width; i++)
                for (int j = 0; j < height; j++)
                    if (obstacles[i][j]) c++;
            return c;
        }
    }

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

        cityMap = new CityMap(width, height);
        // resets all the stimulation states
        tick = 0;
        // throw new UnsupportedOperationException("Not implemented yet");

        // Intializes the grid with the width and height the user has inputted
        // making obstacle grid all false where
        // FALSE REPRESENTS AN EMPTY SPACE IN THE GRID
        obstacles = new boolean[width][height];

        // Resets all the states
        stationCount = 0;
        unitCount = 0;
        incidentCount = 0;
        
        // Resets static ID generators
        Station.nextStationID = 1;
        Unit.nextUnitID = 1;
        Incident.nextIncidentID = 1;
    }
    

    @Override
    public int[] getGridSize() {
        // TODO: implement
        // returns the valid stored width and height;
        return new int[]{cityMap.width, cityMap.height};
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void addObstacle(int x, int y) throws InvalidLocationException {
        // TODO: implement
        // Checks if the obstacles coords are within the city grid
        if (!cityMap.isInBounds(x, y)) {
            // If not the message
            throw new InvalidLocationException("Your position for the obstacle at (" + x + ", " + y + ") is invalid!!");
        }
        // throw new UnsupportedOperationException("Not implemented yet");

        // The obstacle has now been placed and is registered as TRUE
        // on the city map
        cityMap.addObstacle(x, y);
    }

    @Override
    public void removeObstacle(int x, int y) throws InvalidLocationException {

        if (!cityMap.isInBounds(x, y)) {
            throw new InvalidLocationException("Your position for the obstacle at (" + x + ", " + y + ") is invalid!!");
        }
        // throw new UnsupportedOperationException("Not implemented yet");

        // Now the cell in the array is unblocked and is set back to FALSE
        cityMap.removeObstacle(x, y);
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
        int maxUnits = 1;
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
        if (!cityMap.isInBounds(x, y) || cityMap.isBlocked(x, y)) {
            // Ensures that the position for the station is on the grid
            // and is not on a position where an obstacle has been added
            throw new InvalidLocationException("The location you have put (" + x + ", " + y + ") is invalid!!");
        }
        
        // Ensures that the Station count never exceeds the maximum number of stations
        if (stationCount >= MAX_STATIONS)
            throw new CapacityExceededException("Maximum stations reached");

        // New station object is now made if the above is passed
        // where the maxUnits is a default 1
        Station s = new Station(x, y, name, 1);
        // add station to the array and increment station count
        stations[stationCount++] = s;

        // return the valid station id to the unique station
        return s.id;

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void removeStation(int stationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        // index that tracks the position of al current stations in the array
        int idx = -1;
        // loop through all the stations to identify the one that matches the id
        for (int i = 0; i < stationCount; i++) {
            if (stations[i].id == stationId) {
                // checks if the station jas any units
                if (stations[i].unitCount > 0) {
                    throw new IllegalStateException("Station : " + stationId + " still has units and therefore cannot be removed!");
                }
                // stores the index of the station we can rightfully remove
                idx = i;
                break;
            }
        }
        // when the station is not found then the throw statement
        if (idx == -1) {
            throw new IDNotRecognisedException("StationID : " + stationId + " is not valid.");
        }

        // Deterministic removal 
        // Removing the station from the array and shofting all remaining 
        // stationd down by one
        for (int i = idx; i < stationCount - 1; i++) {
            stations[i] = stations[i + 1];
        }

        // ignores the last decrement on the station count
        stations[--stationCount] = null;
        
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setStationCapacity(int stationId, int maxUnits) throws IDNotRecognisedException, InvalidCapacityException {
        // TODO: implement
        Station s = null;
        // loops through all the station IDs till we get a match
        for (int i = 0; i < stationCount; i++) {
            if (stations[i].id == stationId) {
                s = stations[i];
                // Cannot reduce the maxUnits to be less than the current unit count
                if (s == null ){
                    // if the station ID is not found
                    throw new IDNotRecognisedException("StationID : " + stationId + " is not valid.");
                } if (maxUnits <= 0 || maxUnits < s.unitCount){
                    throw new InvalidCapacityException("Invalid capacity");
                }
                s.maxUnits = maxUnits;
            }
        // throw new UnsupportedOperationException("Not implemented yet");
        }
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
        int homeStationId;
        // Assigned default incident ID
        int incidentId = -1;
        private static int nextUnitID = 1;
        // remaining incident ticks
        int iTR = 0;


        // Constructor used by subclasses 
        Unit(int homeStationId, int x, int y, UnitType type){
            this.id = nextUnitID++;
            this.homeStationId = homeStationId;
            this.type = type;
            this.x = x;
            this.y = y;
            this.status = UnitStatus.IDLE;
            
            
        }

        // Determines if the unit can handle the given incident type
        abstract boolean canHandle(IncidentType type);
        
        // ticks required to resolve the incident occuring
        abstract int getTicksToResolve(int severity);
    }

    // Subclass of Ambulance 
    private static class Ambulance extends Unit {
        Ambulance(int homeStationId, int x, int y) {
            super(homeStationId, x, y, UnitType.AMBULANCE);
        }
        // Handles the medical incidents
        @Override
        boolean canHandle(IncidentType type) {
            return type == IncidentType.MEDICAL;
        }
        // Fastest resolution time of 2 ticks
        @Override
        int getTicksToResolve(int severity) {
            return 2;
        }
    }
    
    // Subclass of fire engines
    private static class FireEngine extends Unit {
        FireEngine(int homeStationId, int x, int y) {
            super(homeStationId, x, y, UnitType.FIRE_ENGINE);
        }
        // Handles fire related incidents
        @Override
        boolean canHandle(IncidentType type) {
            return type == IncidentType.FIRE;
        }
        // Longest resolution time of 4 ticks
        @Override
        int getTicksToResolve(int severity) {
            return 4;
        }
    }
    // Subclass of Police cars
    private static class PoliceCar extends Unit {
        PoliceCar(int homeStationId, int x, int y) {
            super(homeStationId, x, y, UnitType.POLICE_CAR);
        }
        // Handles incidents of crime
        @Override
        boolean canHandle(IncidentType type) {
            return type == IncidentType.CRIME;
        }
        // Medium resolution time
        @Override
        int getTicksToResolve(int severity) {
            return 3;
        }
    }
    // factory method to create units  from a station
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
        // Finding a station to add a unit
        Station s = null;
        for (int i = 0; i < stationCount; i++){
            if(stations[i].id == stationId){
                s = stations[i];
                break;
            }
        }
        // Ensures that the station ID isn't null
        if(s == null){
            throw new IDNotRecognisedException("StationID : " + stationId + " is not valid.");
        }
        // Checks if the station has any space
        if(s.unitCount >= s.maxUnits){
            throw new IllegalStateException("The station: " + stationId + " is at its maximum capacity");
        }
        // Checks if the maximum number of units is achieved
        if (unitCount >= MAX_UNITS){
            throw new CapacityExceededException("Maximum units reached");
        }

       // Creates Subclass
       Unit u = createUnit(type, s);
       
       units[unitCount++] = u;
       station.unitCount++;
       return u.id;
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void decommissionUnit(int unitId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        // Intialise index to -1, holding
        Unit u = null;
        // the position of the unit in the units array
        int idx = -1;

        // We find the unit by looping through all units
        for(int i = 0; i < unitCount; i++) {
            if(units[i].id == unitId){
                u = units[i];
                idx = i;
                break;
            }
        }
        // If the unit was not found then..
        if(u == null){
            throw new IDNotRecognisedException("Unit ID: " + unitId + " is not found");
        }
    

        // makes sure the unit is not occupied
        if(u.status == UnitStatus.EN_ROUTE || u.status == UnitStatus.AT_SCENE){
            throw new IllegalStateException("The unit is currently in use!");
        }
        // Removes the unit count from the original station
        for( int i = 0; i < stationCount; i++){
            if(stations[i].id == u.homeStationId){
                stations[i].unitCount--;
            }
        }
        // Deterministic removal shifts all to the left
        for (int i = idx; i < unitCount - 1; i++){
            units[i] = units[i + 1];
        }
        
        units[--unitCount] = null;
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void transferUnit(int unitId, int newStationId) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        Unit u = null;

        // We find the unit in the units array
        for(int i = 0; i < unitCount; i++) {
            if(units[i].id == unitId){
                // Assigns the unit here.
                u = units[i];
            }
        }
        // When unit is not found...
        if(u == null){
            throw new IDNotRecognisedException("The unit ID " + unitId + " is not found" );
        }
        // Only Idle vehicles can be transferred
        // so for everything other that IDLE the following...
        if(u.status != UnitStatus.IDLE){
            throw new IllegalStateException("The unit is currently occupied!");
        }

        // finds the station to transfer a unit
        Station newStation = null;
        Station oldStation = null;
        // Searches whole arrays
        for (int i = 0; i < stationCount; i++) {
            if (stations[i].id == newStationId) {
                newStation = stations[i];
            }else if (stations[i].id == u.homeStationId){
                oldStation = stations[i];
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
       if (oldStation != null) {
        oldStation.unitCount--;
       }
       newStation.unitCount++;
       // Returns new stationID and it's position
       u.homeStationId = newStationId;
       u.x = newStation.x;
       u.y = newStation.y;
       

        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setUnitOutOfService(int unitId, boolean outOfService) throws IDNotRecognisedException, IllegalStateException {
        // TODO: implement
        // Finds the unit ID
        Unit u = null;
        for (int i = 0; i < unitCount; i++) {
            if (units[i].id == unitId) {
                u = units[i];
            }
        }
        // if unit not found then..
        if (u == null) {
            throw new IDNotRecognisedException("Unit ID " + unitId + " not found.");
        }
        if (outOfService) {
            // Can only mark OUT_OF_SERVICE if IDLE
            if (u.status != UnitStatus.IDLE) {
                throw new IllegalStateException("Unit " + unitId + " is not IDLE and cannot go out of service.");
            }
            // Marks the unit as out of service
            u.status = UnitStatus.OUT_OF_SERVICE;
        } 
        // Returning to service makes unit IDLE 
        else {
            if (u.status == UnitStatus.OUT_OF_SERVICE) {
                u.status = UnitStatus.IDLE;
            }
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

        // Sorts the IDs in ascending order deterministic
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
        Unit u = null;
        for (int i = 0; i < unitCount; i++) {
            if (units[i].id == unitId) { u = units[i]; break; }
        }
        if (u == null) throw new IDNotRecognisedException("Unit ID " + unitId + " not found");
        String work = (u.status == UnitStatus.AT_SCENE) ? " WORK=" + u.iTR : "";
        return "U#" + u.id + " TYPE=" + u.type + " HOME=" + u.homeStationId +
               " LOC=(" + u.x + "," + u.y + ")" + " STATUS=" + u.status +
               " INCIDENT=" + (u.incidentId == -1 ? "-" : u.incidentId) + work;
        }

        
        //throw new IDNotRecognisedException("Unit ID is not found");
    
        // throw new UnsupportedOperationException("Not implemented yet");
    
    // Maximum number of units
    private static final int MAX_INCIDENTS = 200;
    // New array for the unit
    private Incident[] incidents = new Incident[MAX_INCIDENTS];
    private int incidentCount = 0;

    private static class Incident {
        // Incident id
        int id;
        // Incident position x and y
        int x;
        int y;
        // What type of incident it is
        IncidentType type;
        // What status the incident is
        IncidentStatus status;
        // Severity of the incident
        int severity;
        // Unit assigned to incident
        int assignedUnitId = -1;
        private static int nextIncidentID = 1;

        Incident(int severity, int x, int y, IncidentType type){
            this.id = nextIncidentID++;
            this.severity = severity;
            this.type = type;
            this.x = x;
            this.y = y;
            this.status = IncidentStatus.REPORTED;
        }
    }

    

    
    @Override
    public int reportIncident(IncidentType type, int severity, int x, int y) throws InvalidSeverityException, InvalidLocationException {
        // TODO: implement
        if(type == null){
            throw new InvalidSeverityException("Incident type cannot be null");
        }

        // Checks valid severity
        if (severity < 1 || severity > 5) {
            throw new InvalidSeverityException("Invalid severity of " + severity + ".");
        }

        // Location in bounds and not blocked
        if (!cityMap.isInBounds(x, y) || cityMap.isBlocked(x, y)) {
            throw new InvalidLocationException("The location you have put (" + x + ", " + y + ") is out of bounds!");
        }
        
        // Ensures the incident doesn't exceed 200
        if (incidentCount >= MAX_INCIDENTS) {
            throw new CapacityExceededException("Maximum incidents reached");
        }

        // New station object is now made if the above is passed
        // where the maxUnits is a default 1
        Incident inc = new Incident(severity, x, y, type);
        // add station to the array and increment station count
        incidents[incidentCount++] = inc;

        // return the valid station id to the unique station
        return inc.id;

        
    }

    @Override
    public void cancelIncident(int incidentId) throws IDNotRecognisedException, IllegalStateException {

        Incident inc = null;
        for (int i = 0; i < incidentCount; i++){
            if (incidents[i].id == incidentId){
                inc = incidents[i];
                break;
            }
        }

        if (inc == null) {
            throw new IDNotRecognisedException(
                "Incident ID " + incidentId + " not found.");
        }

        
        // In order for the unit be dispatched
        if (inc.status != IncidentStatus.REPORTED && inc.status != IncidentStatus.DISPATCHED) {
            throw new IllegalStateException("Incident status must be REPORTED or DISPATCHED.");}
        // return assigned unit if one exists
        if (inc.assignedUnitId != -1) {
            for (Unit u : units) {
                if (u != null && u.id == inc.assignedUnitId) {
                    u.status = UnitStatus.IDLE;
                    u.incidentId = -1;
                    break;
                }
            }
        }
        
        inc.status = IncidentStatus.CANCELLED;
        inc.assignedUnitId = -1;

        // TODO: implement
        // throw new UnsupportedOperationException("Not implemented yet");
    }


    @Override
    public void escalateIncident(int incidentId, int newSeverity) throws IDNotRecognisedException, InvalidSeverityException, IllegalStateException {
        // TODO: implement

        // Finds the incident ID
        Incident inc = null;
        for (int i = 0; i < incidentCount; i++) {
            if (incidents[i].id == incidentId) {
                inc = incidents[i];
                break;
            }
            
        }
        if (inc == null) throw new IDNotRecognisedException("Incident ID " + incidentId + " not found.");

        // Checks valid severity
        if (newSeverity < 1 || newSeverity > 5) {
            throw new InvalidSeverityException("Invalid severity of " + newSeverity + ".");
        }

        if (inc.status == IncidentStatus.RESOLVED || inc.status == IncidentStatus.CANCELLED) {
            throw new IllegalStateException("Incident status cannot be RESOLVED or CANCELLED!");
        } 

        inc.severity = newSeverity;
        
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int[] getIncidentIds() {

        // TODO: implement
        // New Array that will hold all active Incident IDs
        int[] ids = new int[incidentCount];

        // Loops through all incidents and copies their IDs into the array
        for(int i = 0; i < incidentCount; i++) {
            ids[i] = incidents[i].id;
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

        // Return the sorted array of incident IDs
        return ids;
        //throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String viewIncident(int incidentId) throws IDNotRecognisedException {
        // TODO: implement

        Incident inc = null;
        for (int i = 0; i < incidentCount; i++) {
            if (incidents[i].id == incidentId) {
                inc = incidents[i];
                break;
            }
        }

        if (inc == null) {
            throw new IDNotRecognisedException("Incident ID " + incidentId + " not found!");
        }

        String unitStr = inc.assignedUnitId == -1 ? "-" : "" + inc.assignedUnitId;

        return "I#" + inc.id +
                " TYPE=" + inc.type +
                " SEV=" + inc.severity +
                " LOC=(" + inc.x + "," + inc.y + ")" +
                " STATUS=" + inc.status + 
                " UNIT=" + (inc.assignedUnitId == -1 ? "-" : inc.assignedUnitId);
            
        
        // throw new UnsupportedOperationException("Not implemented yet");
    }

    
    @Override
    public void dispatch() {
        // TODO: implement
        
        for (int i = 0; i < incidentCount; i++) {
            Incident inc = incidents[i];
            if (inc.status != IncidentStatus.REPORTED) continue;
            Unit bestUnit = null;
            int minDist = Integer.MAX_VALUE;
            for (int j = 0; j < unitCount; j++) {
                Unit u = units[j];
                if (!u.canHandle(inc.type) || u.status != UnitStatus.IDLE) continue;
                int dist = Math.abs(u.x - inc.x) + Math.abs(u.y - inc.y);
                if (dist < minDist || (dist == minDist && (bestUnit == null || u.id < bestUnit.id))) {
                    minDist = dist;
                    bestUnit = u;
                }
            }
            if (bestUnit != null) {
                bestUnit.status = UnitStatus.EN_ROUTE;
                bestUnit.incidentId = inc.id;
                inc.status = IncidentStatus.DISPATCHED;
                inc.assignedUnitId = bestUnit.id;
            }
        }
    }

        // throw new UnsupportedOperationException("Not implemented yet");
    
    @Override
    public void tick() {
        tick++; // increment global tick count
        
        // Move units that are en route
        for (int i = 0; i < unitCount; i++) {
            Unit u = units[i];
            if (u.status != UnitStatus.EN_ROUTE) continue;
            
            // Find assigned incident
            Incident inc = null;
            for (int j = 0; j < incidentCount; j++) {
                if (incidents[j].id == u.incidentId) {
                    inc = incidents[j];
                    break;
                }
            }
            if (inc == null) continue;

            // Move one step towards the incident
            moveUnit(u, inc.x, inc.y);

            // Check if unit has arrived
            int dist = Math.abs(u.x - inc.x) + Math.abs(u.y - inc.y);
            if (dist == 0) {
                u.status = UnitStatus.AT_SCENE;
                u.iTR = u.getTicksToResolve(inc.severity);
                inc.status = IncidentStatus.IN_PROGRESS;
            }
        }

        // 2. Decrement work ticks for units at scene
        for (int i = 0; i < unitCount; i++) {
            Unit u = units[i];
            if (u.status != UnitStatus.AT_SCENE) continue;

            u.iTR--;
            if (u.iTR <= 0) {
                u.status = UnitStatus.IDLE;

                // Mark incident as resolved
                for (int j = 0; j < incidentCount; j++) {
                    Incident inc = incidents[j];
                    if (inc.id == u.incidentId) {
                        inc.status = IncidentStatus.RESOLVED;
                        inc.assignedUnitId = -1;
                        break;
                    }
                }
                u.incidentId = -1;
            }
        }
    }

    
    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("TICK=").append(tick).append("\n");
        sb.append("STATIONS=").append(stationCount)
                .append(" UNITS=").append(unitCount)
                .append(" INCIDENTS=").append(incidentCount)
                .append(" OBSTACLES=").append(cityMap.countObstacles()).append("\n");

        sb.append("INCIDENTS\n");
        int[] incIds = getIncidentIds();
        for (int id : incIds) {
            try { sb.append(viewIncident(id)).append("\n"); } catch(Exception e){}
        }

        sb.append("UNITS\n");
        int[] unitIds = getUnitIds();
        for (int id : unitIds) {
            try { sb.append(viewUnit(id)).append("\n"); } catch(Exception e){}
        }

        return sb.toString().trim();
    }


    private void moveUnit(Unit u, int targetX, int targetY) {
        // Defines North East South West
        // as {dx, dy}
        int[][] dirs = {{0,-1},{1,0},{0,1},{-1,0}}; // N, E, S, W
        // Initialize best move as unit's cyrrent position
        int bestX = u.x, bestY = u.y;
        // Calculate current distance to target incident
        int minDist = Math.abs(u.x - targetX) + Math.abs(u.y - targetY);
        // Finds moves that reduces distance best to target
        for (int[] dir : dirs) {
            int nx = u.x + dir[0], ny = u.y + dir[1];

            // Skips move if out of bounds or blocked
            if (!cityMap.isInBounds(nx, ny) || cityMap.isBlocked(nx, ny)) continue;
            // calculate distance if we move to this cell
            int d = Math.abs(nx - targetX) + Math.abs(ny - targetY);
            // Picks first direction that reduces distance best
            if (d < minDist) { bestX = nx; bestY = ny; minDist = d; break; }
        }
        
        // If no reduced distance, move first legal move
        if (bestX == u.x && bestY == u.y) {
            for (int[] dir : dirs) {
                int nx = u.x + dir[0], ny = u.y + dir[1];
                if (!cityMap.isInBounds(nx, ny) || cityMap.isBlocked(nx, ny)) continue;
                bestX = nx; bestY = ny; break;
            }
        }
        // update units position
        u.x = bestX; u.y = bestY;
    }
}