package com.g2o.obra;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.DateFormatConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelReader {
public  static  String VALOR_INCORRETO= " VALOR INCORRETO";
    public static  Map<Long,List<Evento>> readFolha(String filePath) {
        List<String[]> data = new ArrayList<>();
        Map<Long,List<Evento>> mapFolha =  new HashedMap<>(); 
        Map<Long,Boolean>funcionarioJaIncluido = new HashedMap<>();
        	
        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet

            Iterator<Row> rowIterator = sheet.iterator();
            List<Evento>listaEvento =  new ArrayList<>();		
            		
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                Long idFuncionario = null;
                List<String> rowData = new ArrayList<>();

                if(row.getRowNum() >= 3) {
                	
                Evento evento = new Evento();	
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    rowData.add(getCellValueAsString(cell, workbook));
                  
                    if(cell.getColumnIndex() == 0) {
                    	idFuncionario = Long.parseLong(getCellValueAsString(cell, workbook).replace(".0",""));
                    	
                    	if(funcionarioJaIncluido.get(idFuncionario) == null){
                    		listaEvento  =  new  ArrayList<>();
                    		funcionarioJaIncluido.put(idFuncionario, true);
                    	}
                    }
                    
                    if(cell.getColumnIndex() == 5) {
                    	evento.setCodigo(Integer.parseInt((getCellValueAsString(cell, workbook).replace(".0",""))));
                    	
                    }
                    
                    if(cell.getColumnIndex() == 6) {
                    	evento.setDescricao(getCellValueAsString(cell, workbook));
                    }
                    
                    if(cell.getColumnIndex() == 7) {
                    	evento.setValor(new BigDecimal(getCellValueAsString(cell, workbook)));
                    	 
                    }
                    
                }
                
                if(evento.getCodigo() != null && evento.getCodigo() > 0 ) {
                	listaEvento.add(evento);
                }
                if(mapFolha.get(idFuncionario) == null) {
                	mapFolha.put(idFuncionario, listaEvento);
                }
                data.add(rowData.toArray(new String[0]));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return mapFolha;
    }

    private static String getCellValueAsString(Cell cell,Workbook workbook) {
    	 if (cell.getCellType() == CellType.FORMULA) {
             FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
             CellValue cellValue = evaluator.evaluate(cell);

             switch (cellValue.getCellType()) {
                 case STRING:
                    return  cellValue.getStringValue();
                 case NUMERIC:
                    return String.valueOf(cellValue.getNumberValue());
                   
                 // Outros casos conforme necessário
             }
    	 }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case BLANK:
            case _NONE:
                return "";
            default:
                return "";
        }
    }

    
	public static List<Ponto> readPonto(String filePath) {
		List<Ponto> listaPonto = new ArrayList<>();
		
		try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
			Sheet sheet = workbook.getSheetAt(0); // Assuming the data is in the first sheet
			DataFormatter dataFormatter = new DataFormatter();
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				if (row.getRowNum() >= 7) {

					Ponto ponto = new Ponto();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						if (cell.getColumnIndex() == 1) {
							ponto.setId(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook)) ? Long.parseLong(getCellValueAsString(cell, workbook).replace(".0", "")) : null);
						}
						
						
						
						if (cell.getColumnIndex() == 2) {
							ponto.setNome(getCellValueAsString(cell, workbook));
							if(ponto.getNome().equals("JOAQUIM RIBEIRO DE SOUSA")){
								System.out.println("encontrou");
							}
						}
						if (cell.getColumnIndex() == 3) {
							ponto.setHn(!getCellValueAsString(cell, workbook).isEmpty()&& !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")) : null);
						}

						if (cell.getColumnIndex() == 4) {
							ponto.setDsr(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook))? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						}

						if (cell.getColumnIndex() == 5) {
							
							  if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setHoraExtra60(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setHoraExtra60(!getCellValueAsString(cell, workbook).isEmpty()&& !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")) : null);
						       }
						 }

						if (cell.getColumnIndex() == 6) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setHoraExtra100(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setHoraExtra100(!getCellValueAsString(cell, workbook).isEmpty()&& !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						       }
						 }

						if (cell.getColumnIndex() == 7) {
						
					        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
					           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
					           ponto.setAdicionalNoturno(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
					   			
					       }else {
					    		ponto.setAdicionalNoturno(!getCellValueAsString(cell, workbook).isEmpty()&& !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
								
					        }
						}
						if (cell.getColumnIndex() == 8) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setAdicionalNoturno60(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setAdicionalNoturno60(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook))?new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						
						       }
						}	 
						if (cell.getColumnIndex() == 9) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setHeNoturno60(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setHeNoturno60(!getCellValueAsString(cell, workbook).isEmpty()&& !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						
						       }
						 }
						if (cell.getColumnIndex() == 10) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setHeNoturno100(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setHeNoturno100(!getCellValueAsString(cell, workbook).isEmpty()&& !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						       }
						}
						if (cell.getColumnIndex() == 11) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setHeIntervalo(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setHeIntervalo(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook))? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						       }
						 }

						if (cell.getColumnIndex() == 12) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setHeInterjornada(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setHeInterjornada(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook))? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						       }
						 }
						if (cell.getColumnIndex() == 13) {
							 if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setFalta(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setFalta(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook)) ? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						       }
						 }
						if (cell.getColumnIndex() == 14) {
							ponto.setDsrFalta(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook))? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						}

						if (cell.getColumnIndex() == 15) {
							if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
						           String valorAtualizado =  alterarFormatacaoTipoDataInvalido(workbook, ponto, cell, dataFormatter);
						           ponto.setAtrazo(!valorAtualizado.isEmpty()&& !verificaCaracteres(valorAtualizado.replace(":", "")) ? new BigDecimal(valorAtualizado.replace(":", ".")): null);
						   			
						       }else {
						    	   ponto.setAtrazo(!getCellValueAsString(cell, workbook).isEmpty() && !verificaCaracteres(getCellValueAsString(cell, workbook))? new BigDecimal(getCellValueAsString(cell, workbook).replace(":", ".")): null);
						       }
						}

					}

					listaPonto.add(ponto);

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

        return listaPonto;
    }

	private static String alterarFormatacaoTipoDataInvalido(Workbook workbook, Ponto ponto, Cell cell,
			DataFormatter dataFormatter) {
		String valorFomatado;
		// If the cell contains a date, format it as [h]:mm:ss
		CreationHelper creationHelper = workbook.getCreationHelper();
		short dateFormat = creationHelper.createDataFormat().getFormat("[h]:mm:ss");

		CellStyle style = workbook.createCellStyle();
		style.setDataFormat(dateFormat);

		cell.setCellStyle(style);
		valorFomatado = dataFormatter.formatCellValue(cell);
		
		// Encontrar a posição dos dois primeiros pontos
		int firstColonIndex = valorFomatado.indexOf(':');
		int secondColonIndex = valorFomatado.indexOf(':', firstColonIndex + 1);
		String extractedSubstring ="";
		// Verificar se há pelo menos dois pontos na string
		if (firstColonIndex != -1 && secondColonIndex != -1) {
		    // Extrair "137:53" (do início até o segundo ponto)
		     extractedSubstring = valorFomatado.substring(0, secondColonIndex);
		} 
		
		return extractedSubstring;
		
	}
    
	 private String getFormattedCellValue(Cell cell, Workbook workbook) {
	        DataFormatter dataFormatter = new DataFormatter();

	        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
	            // If the cell contains a date, format it as [h]:mm:ss
	            CreationHelper creationHelper = workbook.getCreationHelper();
	            short dateFormat = creationHelper.createDataFormat().getFormat("[h]:mm:ss");

	            CellStyle style = workbook.createCellStyle();
	            style.setDataFormat(dateFormat);

	            cell.setCellStyle(style);
	        }

	        return dataFormatter.formatCellValue(cell);
	    }
	   
	
    public static void main(String[] args) {
        // Specify the file path of the Excel file to read
        String filePath = "C:\\g2o\\folha.xlsx";
        String filePath2 = "C:\\g2o\\ponto.xlsx";

        System.out.println("Iniciando leitura arquivo Folha");
        Map<Long,List<Evento>> mapFolha = readFolha(filePath);
        System.out.println("Finalizado leitura arquivo Folha");
        
        System.out.println("Iniciando leitura arquivo Ponto");
        List<Ponto> listaPonto = readPonto(filePath2);
        System.out.println("Finalizado leitura arquivo Ponto");
        
        List<Inconsistencia> listaInconsistencia = new ArrayList<Inconsistencia>();
        Inconsistencia inconsistencia =  new Inconsistencia();
        try {
        
     // Cria um objeto File associado ao caminho especificado
        File arquivo = new File("C:\\g2o\\Inconsistencia.txt");

        // Cria objetos FileWriter e BufferedWriter para escrever no arquivo
        FileWriter escritor = new FileWriter(arquivo);
        BufferedWriter bufferEscrita = new BufferedWriter(escritor);
        StringBuilder textoConcatenado = new StringBuilder();
        System.out.println("Iniciando verificação de inconsistências");
        // Iterando sobre as entradas do mapa
        

            for(Ponto ponto : listaPonto) {
            	inconsistencia = new Inconsistencia();
            	inconsistencia.setMatricula(ponto.getId());
        		inconsistencia.setNome(ponto.getNome());
            	            	
            	boolean existeInconsistencia = false;
            	Map<Integer, Evento> mapEvento = new HashMap<>();
            	if (ponto.getId() != null && mapFolha.get(ponto.getId())!= null) {
            		
	            		List<Evento> listaEvento =  mapFolha.get(ponto.getId());
	            		
	            		
	            		 for (Evento evento : listaEvento) {
	            	            Integer codigo = evento.getCodigo();
	
	            	            // Se não, criar uma nova lista e adicionar ao Map
	            	            mapEvento.put(codigo, evento);
	            	            }
	            	 
	            			 if (ponto.getHn() != null &&  !ponto.getHn().equals(new BigDecimal("1000.0")) && ponto.getHn().compareTo(BigDecimal.valueOf(0.0)) != 0) {//HOra Normal
	                    		 Evento  evento = mapEvento.get(2);
	                    		int retorno  =  evento != null && evento.getValor() != null  ? ponto.getHn().compareTo(evento.getValor()):-1;
	                    		existeInconsistencia = retorno != 0;
	                    		
	                    		boolean isValorValido = evento != null && evento.getValor() != null;
	                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
	                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO +" HN Ponto="+ponto.getHn()+" Folha="+ valor : " " );
	                    	
	                    	 }
	                    	                    	 
	                    	
	                    	  if(ponto.getDsr() != null &&  !ponto.getDsr().equals(new BigDecimal("1000.0"))&& ponto.getDsr().compareTo(BigDecimal.valueOf(0.0)) != 0 ) {
		                    		Evento  evento = mapEvento.get(3);
		                    		int retorno  = ponto.getDsr() != null &&  evento != null && evento.getValor() != null? ponto.getDsr().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? "DSR Correto" : VALOR_INCORRETO +" DSR Ponto="+ponto.getDsr()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO +" DSR Ponto="+ponto.getDsr()+" Folha=" +valor : "");
		                    	}
	                    	  
	                    	  
	                    	  if (ponto.getHoraExtra60() != null  && ponto.getHoraExtra60().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(194);
		                    		int retorno  = ponto.getHoraExtra60() != null &&  evento != null && evento.getValor() != null ? ponto.getHoraExtra60().compareTo(evento.getValor()): -1;
		                    		//System.out.println(retorno == 0 ? "HE60 Correto" : VALOR_INCORRETO+" HE60 Ponto="+ponto.getHoraExtra60()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ?  VALOR_INCORRETO+" HE60 Ponto="+ponto.getHoraExtra60()+" Folha="+valor:" ");
		                    	}
	                    	  
	                    	  if (ponto.getHoraExtra100() != null&& ponto.getHoraExtra100().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(82);
		                    		int retorno  = ponto.getHoraExtra100() != null &&  evento != null && evento.getValor() != null ? ponto.getHoraExtra100().compareTo(evento.getValor()): -1;
		                    		//System.out.println(retorno == 0 ? "HE60 Correto" : VALOR_INCORRETO+" HE60 Ponto="+ponto.getHoraExtra60()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ?  VALOR_INCORRETO+" HE100 Ponto="+ponto.getHoraExtra100()+" Folha="+valor:" ");
		                    	}
	                    	  
	                    	  if(ponto.getAdicionalNoturno()!= null && ponto.getAdicionalNoturno().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(1252);
		                    		int retorno  =  ponto.getAdicionalNoturno()!= null &&  evento != null && evento.getValor() != null ? ponto.getAdicionalNoturno().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? "ADNoturno Correto" : VALOR_INCORRETO+" ADNoturno Ponto="+ponto.getAdicionalNoturno()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" ADNoturno Ponto="+ponto.getAdicionalNoturno()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getAdicionalNoturno60()!= null && ponto.getAdicionalNoturno60().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(1479);
		                    		int retorno  = ponto.getAdicionalNoturno60()!= null &&  evento != null && evento.getValor() != null ? ponto.getAdicionalNoturno60().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? "ADNoturno60 Correto" :  VALOR_INCORRETO+" ADNoturno60 Ponto="+ponto.getAdicionalNoturno60()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" ADNoturno60 Ponto="+ponto.getAdicionalNoturno60()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getHeNoturno60() != null && ponto.getHeNoturno60().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		Evento  evento = mapEvento.get(1431);
		                    		int retorno  =  ponto.getHeNoturno60() != null &&  evento != null && evento.getValor() != null  ? ponto.getHeNoturno60().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? "HE Noturno60 Correto" :  VALOR_INCORRETO+" HE Noturno60 Ponto="+ponto.getHeNoturno60()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" HE Noturno60 Ponto="+ponto.getHeNoturno60()+" Folha="+valor: "" );
		                    	}
	                    	 
	                    	  
	                    	  if(ponto.getHeNoturno100() != null && ponto.getHeNoturno100().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(1430);
		                    		int retorno  =  ponto.getHeNoturno100() != null &&  evento != null && evento.getValor() != null ? ponto.getHeNoturno100().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? "HENoturno100 Correto" :  VALOR_INCORRETO+" HENoturno100 Ponto="+ponto.getHeNoturno100()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		inconsistencia.setTexto(retorno != 0 ?  VALOR_INCORRETO+" HENoturno100 Ponto="+ponto.getHeNoturno100()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getHeIntervalo() != null && ponto.getHeIntervalo().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(1470);
		                    		int retorno  =  ponto.getHeIntervalo() != null &&  evento != null && evento.getValor() != null ? ponto.getHeIntervalo().compareTo(evento.getValor()):-1 ;
		                    		//System.out.println(retorno == 0 ? "He Intervalo Correto" :  VALOR_INCORRETO+" He Intervalo Ponto="+ponto.getHeIntervalo()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" He Intervalo Ponto="+ponto.getHeIntervalo()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getHeInterjornada() != null  && ponto.getHeInterjornada().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(1471);
		                    		int retorno  = ponto.getHeInterjornada() != null &&  evento != null && evento.getValor() != null  ? ponto.getHeInterjornada().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? " HeInterjornada Correto" :  VALOR_INCORRETO+" HeInterjornada Ponto="+ponto.getHeInterjornada()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ?  VALOR_INCORRETO+" HeInterjornada Ponto="+ponto.getHeInterjornada()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getFalta() != null && ponto.getFalta().compareTo(BigDecimal.valueOf(0.0)) != 0) {
		                    		 Evento  evento = mapEvento.get(1445);
		                    		int retorno  = ponto.getFalta() != null &&  evento != null && evento.getValor() != null ? ponto.getFalta().compareTo(evento.getValor()): -1;
		                    		//System.out.println(retorno == 0 ? "Falta Correto" :  VALOR_INCORRETO+" Falta Ponto="+ponto.getFalta()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" Falta Ponto="+ponto.getFalta()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getDsrFalta() != null && ponto.getDsrFalta().compareTo(BigDecimal.valueOf(0.0)) != 0 ) {
		                    		 Evento  evento = mapEvento.get(1447);
		                    		int retorno  =  ponto.getDsrFalta() != null &&  evento != null && evento.getValor() != null  ? ponto.getDsrFalta().compareTo(evento.getValor()):-1;
		                    		//System.out.println(retorno == 0 ? "DsrFalta Correto" :  VALOR_INCORRETO+" DsrFalta Ponto="+ponto.getDsrFalta()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" DsrFalta Ponto="+ponto.getDsrFalta()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  if(ponto.getAtrazo() != null  && ponto.getAtrazo().compareTo(BigDecimal.valueOf(0.0)) != 0 ) {
		                    		 Evento  evento = mapEvento.get(38);
		                    		int retorno  = ponto.getAtrazo() != null &&  evento != null && evento.getValor() != null ? ponto.getAtrazo().compareTo(evento.getValor()): -1;
		                    		//System.out.println(retorno == 0 ? "Atrazo Correto" :  VALOR_INCORRETO+"Atrazo  Ponto="+ponto.getAtrazo()+" Folha="+evento.getValor());
		                    		existeInconsistencia = retorno != 0;
		                    		
		                    		boolean isValorValido = evento != null && evento.getValor() != null;
		                    		String valor = isValorValido ? evento.getValor().toString() : "INEXISTENTE";
		                    		textoConcatenado.append(retorno != 0 ? VALOR_INCORRETO+" Atrazo  Ponto="+ponto.getAtrazo()+" Folha="+valor: "" );
		                    	}
	                    	  
	                    	  inconsistencia.setTexto(textoConcatenado.toString());
	                    	  textoConcatenado.setLength(0);
	                          if( inconsistencia.getTexto().contains("VALOR INCORRETO")) {
	                          	listaInconsistencia.add(inconsistencia);
	                          }
	                  }
            }
	        System.out.println("Quantidade de Matrículas com Inconsistencia encontradas:"+listaInconsistencia.size());
	        for(Inconsistencia i : listaInconsistencia) {
	        	bufferEscrita.write("Matricula:"+i.getMatricula() + " Nome:" + i.getNome()+" "+ i.getTexto());
	        	bufferEscrita.write("\n");
	        }
	       
	        // Fecha os recursos para liberar o arquivo
	        bufferEscrita.close();
	        escritor.close();
	        
        }
        catch (IOException e) {
            // Trata exceções de entrada/saída, como arquivo não encontrado, etc.
            e.printStackTrace();
            
        }
        
      
    }
    
    public static boolean verificaCaracteres(String str) {
        return str.matches("[a-zA-Z].*");
    }
}
