package GUI;

import java.util.ArrayList;

class IdealTeam {

    //========================= NODE & DATA STRUCTURE INSTANTIATION =========================

    private ArrayList<team> team_cluster;

    // Nodes are constructed to the purpose of an ordered list
    static class team {

        Object name;

        ArrayList<Pair<Object, Pair<Object, Object>>> players; // FORMAT: <player, <season interval, avg points>>

        team(Object name){
            this.name = name;
            players = new ArrayList<>();
        }
    }


    //========================= ORDERED LIST =========================

    // Graph initialization
    IdealTeam() { team_cluster = new ArrayList<>(); }

    // Returns the number of the teams
    int getNumberOfTeams() { return team_cluster.size(); }

    // Returns the number of plays in a given team, will return -1 if not found
    int getNumberOfPlayers( Object team ) {
        try {
            for (IdealTeam.team value : team_cluster) { // searching for team, given a name
                if (value.name.toString().equals(team.toString())) {
                    return value.players.size();
                }
            }
        } catch (Exception e){
            System.out.println("getNumberOfPlayers Error :: " + e.toString());
        }
        return -1;
    }

    // Adds team into the ordered list - checks for repetition
    void addTeam( Object name ){
        try {
            for (IdealTeam.team team : team_cluster) {
                if (team.name.toString().equals(name.toString())) {
                    return;
                }
            }
            team_cluster.add(new team(name));
        } catch (Exception e){
            System.out.println("addTeam Error :: " + e.toString());
        }
    }

    // Adds player into a given team - checks for repetition, and will throw an exception if the given team cannot be found
    void addPlayer( Object team, Pair<Object, Pair<Object, Object>> player){
        try{
            int index = -1; // looking for player's team
            for(int i = 0; i < team_cluster.size(); i++){
                if(team_cluster.get(i).name.toString().equals(team.toString())) {
                    index = i;
                    break;
                }
            }

            if(index == -1) // ensuring a node is found
                throw new NullPointerException(":: Team is wack!");

            for(int i = 0; i < team_cluster.get(index).players.size(); i++){ // checking for player repetition
                if(team_cluster.get(index).players.get(i).getLeft().toString().equals(player.getLeft().toString())) {
                    return;
                }
            }

            if(team_cluster.get(index).players.size() == 0){
                team_cluster.get(index).players.add(player);
                return;
            }

            for(int i = 0; i < team_cluster.get(index).players.size(); i++){ // seasons act at point multipliers
                Pair<Object, Object> player_data = team_cluster.get(index).players.get(i).getRight();
                if(Double.parseDouble(player_data.getLeft().toString()) * Integer.parseInt(player_data.getRight().toString()) <=
                        Double.parseDouble(player.getRight().getLeft().toString()) * Integer.parseInt(player.getRight().getRight().toString())) {
                    team_cluster.get(index).players.add(i, player);
                    return;
                }
            }

        } catch (Exception e){
            System.out.println("addPlayer Error :: " + e.toString());
        }
    }

    // Returns an array list of the first 53 players best suited for a given team.
    // Note, players are already ordered in player lists
    ArrayList<Pair<Object, Pair<Object, Object>>> getIdealTeam( Object team ){
        ArrayList<Pair<Object, Pair<Object, Object>>> output = new ArrayList<>();
        try {

            int index = -1;
            for (int i = 0; i < team_cluster.size(); i++) {
                if (team_cluster.get(i).name.toString().equals(team.toString())) {
                    index = i;
                }
            }

            if(index == -1) // ensuring a node is found
                throw new NullPointerException(":: Team is wack!");

            for(int i = 0; i < 53; i++){ // 53 is the size of a football team
                if(i >= team_cluster.get(index).players.size())
                    return output;
                output.add(team_cluster.get(index).players.get(i));
            }

        } catch (Exception e) {
            System.out.println("getIdealTeam Error :: " + e.toString());
        }
        return output;
    }

    //========================= PRETTY PRINTING & FORMATTING =========================

    void prettyPrintTeams(){
        System.out.println("\n<><><><><><><><><><><><><><><>");
        System.out.println("<<<<<< Ideal Team List >>>>>>>");
        System.out.println("<><><><><><><><><><><><><><><>\n");

        for (IdealTeam.team team : team_cluster) {
            System.out.print("[" + team.name.toString() + "] -> ");
            for (int j = 0; j < team.players.size(); j++) {
                System.out.print("(" + team.players.get(j).getLeft().toString() + ", " + team.players.get(j).getRight().toString() + ") -> ");
            }
            System.out.println("X");
        }

    }

}
