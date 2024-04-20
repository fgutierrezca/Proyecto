package hn.unah.bases.proyecto_playstore.DTOs;

public class CampoDTO {
    
    private String columnName;
    private String columnNameConvert;
    private String dataType;
    private int maxLength;
    private int maxLeghtConvert;
    private String alias;

   

    // Constructor
    public CampoDTO() {
        // Inicializa columnNameConvert con el valor actual de columnName
        this.columnNameConvert = this.columnName;
        // Inicializa maxLeghtConvert con el valor actual de maxLength
        this.maxLeghtConvert = this.maxLength;

        this.alias=this.columnName;
    }

    public int getMaxLength() {
        return maxLength;
    }
    public void setMaxLength(int maxLeght) {
        this.maxLength = maxLeght;
    }
    public String getColumnName() {
        return columnName;
    }
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public String getColumnNameConvert() {
        return columnNameConvert;
    }
    public void setColumnNameConvert(String columnNameConvert) {
        this.columnNameConvert = columnNameConvert;
    }

    public int getMaxLeghtConvert() {
        return maxLeghtConvert;
    }

    public void setMaxLeghtConvert(int maxLeghtConvert) {
        this.maxLeghtConvert = maxLeghtConvert;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void upperColumn(){
        this.columnNameConvert= "UPPER("+this.columnName+") as columnName";
    }

    public void lowerColumn(){
        this.columnNameConvert="LOWER("+this.columnName+") as columnName";
    }

    public void concatValues(String value, String alias){

        this.alias=alias;

        this.columnNameConvert="( "+this.columnName+" || "+" CHR(32) " +" ||'"+value+"' ) as "+alias;
        this.maxLeghtConvert=this.maxLength+value.length()+1;
    }

    public void concatValues(CampoDTO campo, String alias){
        
        this.alias=alias;

        if("VARCHAR2".equals(campo.getDataType())|| "CHAR".equals(campo.getDataType())){
            this.columnNameConvert="( "+this.columnName+" || "+ "CHR(32)"  + "||"+campo.getColumnName()+" ) as "+alias;
            this.maxLeghtConvert=this.maxLength+campo.getMaxLength()+1;
        }
        else {
            this.columnNameConvert="( "+this.columnName+" || "+ "CHR(32)" + "|| TO_CHAR( "+campo.getColumnName()+" )) as "+alias;
            this.maxLeghtConvert=this.maxLength+21;
        } 

    }

    public void Extraer(String periodo, String alias){

        this.alias=alias;
        if(periodo.equals("HOUR")){
            this.columnNameConvert="TO_CHAR(TO_TIMESTAMP( "+this.columnName +"),) as "+alias;
          //  this.columnNameConvert="EXTRACT( "+periodo+" FROM TO_TIMESTAMP( "+this.columnName +") ) as "+alias;
        }else{
            this.columnNameConvert="EXTRACT( "+periodo+" FROM "+this.columnName +" ) as "+alias;
        }

    }
    
}
