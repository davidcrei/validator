package com.g2o.obra;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Funcionario {
	
private Long id;
private String nome;
private List<Evento> evento;

}
