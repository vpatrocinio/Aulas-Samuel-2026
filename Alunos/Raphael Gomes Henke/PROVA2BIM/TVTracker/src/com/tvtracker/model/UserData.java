package com.tvtracker.model;

import java.util.*;

/**
 * Guarda as informações locais do usuário: nome/apelido e suas três listas de séries.
 */
public class UserData {

    private String username;

    private List<Show> favorites = new ArrayList<>();
    private List<Show> watched = new ArrayList<>();
    private List<Show> wantToWatch = new ArrayList<>();

    // Ordenação salva da aba Favoritos
    private SortCriteria favoriteSort = SortCriteria.NOME;

    public UserData() {
    }

    public UserData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Show> getFavorites() {
        return favorites;
    }

    public List<Show> getWatched() {
        return watched;
    }

    public List<Show> getWantToWatch() {
        return wantToWatch;
    }

    // Getter e Setter da ordenação dos Favoritos
    public SortCriteria getFavoriteSort() {
        return favoriteSort;
    }

    public void setFavoriteSort(SortCriteria favoriteSort) {
        this.favoriteSort = favoriteSort;
    }

    public List<Show> getListByType(ListType type) {
        switch (type) {
            case FAVORITES:
                return favorites;

            case WATCHED:
                return watched;

            case WANT_TO_WATCH:
                return wantToWatch;

            default:
                throw new IllegalArgumentException("Tipo de lista inválido: " + type);
        }
    }
    
    
    
    
    /*
    //////////////////////////
    
    SERIALIZAÇÃO DAS INFORMAÇÕES DO CODIGO
    faz userdata virar map pra ser salvo como json 

    //////////////////////////
    */
    public Map<String, Object> toJson() {

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("username", username);
        map.put("favorites", toJsonList(favorites));
        map.put("watched", toJsonList(watched));
        map.put("wantToWatch", toJsonList(wantToWatch));

        // Salva a ordenação escolhida
        map.put("favoriteSort", favoriteSort.name());

        return map;
    }








    private List<Object> toJsonList(List<Show> shows) {

        List<Object> list = new ArrayList<>();

        for (Show s : shows) {
            list.add(s.toStorageJson());
        }

        return list;
    }

    @SuppressWarnings("unchecked")





    /*
    ///////////////////////////////////
     
    DESSERIALIZAÇÃO 
    FAZ OS DADOS JSON VIRAREM OBJETO NO USERDATA NOVAMENTE

    ///////////////////////////////////
    */
    public static UserData fromJson(Map<String, Object> json) {

        UserData ud = new UserData();

        Object u = json.get("username");
        ud.username = u != null ? u.toString() : "Usuário";

        ud.favorites = fromJsonList(json.get("favorites"));
        ud.watched = fromJsonList(json.get("watched"));
        ud.wantToWatch = fromJsonList(json.get("wantToWatch"));

        // Carrega a ordenação salva
        Object sort = json.get("favoriteSort");

        if (sort != null) {
            try {
                ud.favoriteSort = SortCriteria.valueOf(sort.toString());
            } catch (Exception e) {
                ud.favoriteSort = SortCriteria.NOME;
            }
        }

        return ud;
    }



    





    @SuppressWarnings("unchecked")
    private static List<Show> fromJsonList(Object obj) {

        List<Show> list = new ArrayList<>();

        if (obj instanceof List) {

            for (Object o : (List<Object>) obj) {

                if (o instanceof Map) {

                    list.add(Show.fromStorageJson((Map<String, Object>) o));

                }
            }
        }

        return list;
    }
}