package com.g2o.obra;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Ponto {

	private Long id;
	private String nome;
	private BigDecimal hn;
	private BigDecimal dsr;
	private BigDecimal horaExtra60;
	private BigDecimal horaExtra100;
	private BigDecimal adicionalNoturno;
	private BigDecimal adicionalNoturno60;
	private BigDecimal heNoturno60;
	private BigDecimal heNoturno100;
	private BigDecimal heIntervalo;
	private BigDecimal heInterjornada;
	private BigDecimal falta;
	private BigDecimal dsrFalta;
	private BigDecimal atrazo;
	
}
