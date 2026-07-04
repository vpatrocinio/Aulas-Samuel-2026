package Modelos;

public class Serie {
    private String nome;
    private String idioma;
    private double nota;
    private String status;
    private String estreia;
    private String termino;
    private String imagem;
    private String generos;
    private String emissora;

    public Serie() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIdioma(){
        return idioma;
    }

    public void setIdioma(String idioma){
        this.idioma = idioma;
    }

    public Double getNota(){
        return  nota;
    }

    public void setNota(Double nota){
        this.nota =nota;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstreia() {
        return estreia;
    }

    public void setEstreia(String estreia) {
        this.estreia = estreia;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public String getImagem() { return imagem;}

    public void setImagem(String imagem) {this.imagem = imagem;}

    public String getGeneros(){return generos;}

    public void setGeneros(String generos){this.generos = generos;}

    public String getEmissora(){return emissora;}

    public void setEmissora(String emissora){this.emissora = emissora;}

    public String toString(){return nome;}
}