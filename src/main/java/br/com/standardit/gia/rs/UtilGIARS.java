package br.com.standardit.gia.rs;

import java.io.File;

import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import br.com.standardit.Ted;
import br.com.standardit.UtilAutomation;
import br.com.standardit.WaitResult;
import br.com.standardit.util;
import br.com.standardit.pva.ArquivoFiscal;

public class UtilGIARS {
	Screen s = new Screen();
	String watchPath;
	public UtilAutomation utilAut;
	public Ted ted;

	public UtilGIARS(Screen s, String watchPath) {
		this.s = s;
		this.utilAut = new UtilAutomation(s);
		this.watchPath = watchPath;
	}

//--------------------------------------------------------------------------------------------------	
	public boolean Start(String giaPath) {
		WaitResult waitResult = null;
		// FechaNotificacaoDeVersoes();

		String[] cmd = { giaPath };
		try {
			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(cmd);
			util.sleep(60);

//			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
//			if (waitResult.region != null) {
//				rt.exec(cmd);
//				util.sleep(60);
//			}

			Integer cont = 0;
			waitResult = utilAut.WaitFor(1,
					new String[] { "GIA_RS\\TelaInicialAtualizada", "GIA_RS\\TelaInicialAtualizada2" });
			while (waitResult.region == null) {
				waitResult = utilAut.WaitFor(1,
						new String[] { "GIA_RS\\BotaoSimplesDeOK", "GIA_RS\\BotaoSimplesDeOK2" });
				if (waitResult.region != null) {
					util.log.info("[" + util.getMetod() + "] - FecharMsgInicial");
					waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\FecharMsgInicial" });
					if (waitResult.region != null) {
						waitResult.region.click();
						util.sleep(2);
					}

					s.type(Key.ENTER);
					util.sleep(2);

					waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\FecharTelaApuracaoICMS" });
					if (waitResult.region != null) {
						waitResult.region.click();
						util.sleep(2);
						ExcluiGiaGeradas();
					}
				}
				if (cont == 30)
					break;
				waitResult = utilAut.WaitFor(1,
						new String[] { "GIA_RS\\TelaInicialAtualizada", "GIA_RS\\TelaInicialAtualizada2" });
				cont++;
			}

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

//---------------------------------------------------------------------------------------------------------------------------------	
	public void FechaNotificacaoDeVersoes() {

		util.log.info("[" + util.getMetod() + "] - Aguarda msg de Atualizacao concluida");
		WaitResult waitResult = utilAut.WaitFor(5,
				new String[] { "GIA_RS\\Atualizacao_concluida", "GIA_RS\\Atualizacao_concluida2" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Botao_OK", "GIA_RS\\Botao_OK2" });
			waitResult.region.click();
		}

		util.log.info("[" + util.getMetod() + "] - Valida tela de notificao de versoes");
		waitResult = utilAut.WaitFor(2, new String[] { "GIA_RS\\Tela_Notificao" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Tela_Notificao_Botao_OK" });
			if (waitResult.region != null)
				waitResult.region.click();
		}
		// Atualizar_Tabela_UPF
	}

//---------------------------------------------------------------------------------------------------------------------------------	
	public boolean AtivarPrograma() {
		WaitResult waitResult = null;
		Integer cont = 0;
//		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
//		if (waitResult.region != null) {
//			util.log.info("[" + util.getMetod() + "] - Ativa tela de home");
//		}

		waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\TelaInicialAtualizada" });
		while (waitResult.region == null) {
			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\BotaoSimplesDeOK", "GIA_RS\\BotaoSimplesDeOK2" });
			if (waitResult.region != null) {
				util.log.info("[" + util.getMetod() + "] - BotaoOK");
				s.type(Key.ENTER);
				// waitResult.region.click();
			}
			if (cont == 10)
				break;
			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\TelaInicialAtualizada" });
			cont++;
			System.out.println("Contador: " + cont);
		}

//		FecharNotificacaoTabelaUPF2020(10);
//		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
//		if (waitResult.region != null) {
//			util.log.info("[" + util.getMetod() + "] - Ativa tela de home");
//			waitResult.region.click();
//		} else {
//			waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Icone_GIA", "GIA_RS\\Icone_GIA2" });
//			if (waitResult.region != null) {
//				waitResult.region.doubleClick();
//				FecharNotificacaoTabelaUPF2020(5);
//				waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
//				if (waitResult.region != null) {
//					util.log.info("[" + util.getMetod() + "] - Ativa tela de home");
//					waitResult.region.click();
//				} else {
//					return false;
//				}
//			} else {
//				return false;
//			}
//		}
//
//		util.log.info("[" + util.getMetod() + "] - Valida msg de atualizacao de Tabela UPF");
//		waitResult = utilAut.WaitFor(2,
//				new String[] { "GIA_RS\\Consultando_Sefaz_RS", "GIA_RS\\Consultando_Sefaz_RS2" });
//		int contador = 0;
//		while (waitResult.region != null) {
//
//			util.sleep(5);
//			contador++;
//			waitResult = utilAut.WaitFor(2,
//					new String[] { "GIA_RS\\Consultando_Sefaz_RS", "GIA_RS\\Consultando_Sefaz_RS2" });
//			if (contador == 30) {
//				util.log.info("[" + util.getMetod() + "] - Erro na atualização da Tabela...");
//				return false;
//			}
//		}
//
//		FecharNotificacaoTabelaUPF2020(5);
//		FechaNotificacaoDeVersoes();
//
//		util.log.info("[" + util.getMetod() + "] - Ativa tela de home");
//		waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
//		if (waitResult.region != null) {
//			waitResult.region.click();
//		} else {
//			return false;
//		}

		util.log.info("[" + util.getMetod() + "] - Valida arithmetic exception");
		waitResult = utilAut.WaitFor(1, new String[] { "Icone_De_Erro" });
		if (waitResult.region != null) {
			return false;
		}

		return true;
	}

//--------------------------------------------------------------------------------------
	public void FecharNotificacaoTabelaUPF2020(int wait) {

		WaitResult waitResult = null;
		waitResult = utilAut.WaitFor(wait,
				new String[] { "GIA_RS\\Atualizar_Tabela_UPF", "GIA_RS\\Atualizar_Tabela_UPF2" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(wait, new String[] { "GIA_RS\\Botao_YES" });

			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				s.type(Key.ENTER);
				util.sleep(1);
			}

			util.log.info("[" + util.getMetod() + "] - Aguarda msg de Atualizacao concluida");
			waitResult = utilAut.WaitFor(180,
					new String[] { "GIA_RS\\Atualizacao_concluida", "GIA_RS\\Atualizacao_concluida2" });
			if (waitResult.region != null) {
				waitResult = utilAut.WaitFor(wait, new String[] { "GIA_RS\\Botao_OK", "GIA_RS\\Botao_OK2" });
				waitResult.region.click();
			}
		}

	}

//--------------------------------------------------------------------------------------
	public boolean ValidarDados(String enderecoDoArquivo, int tempoMaximoImportacao, String giaUsuario,
			String giaSenha) {

		FechaNotificacaoDeVersoes();

		ExcluiGiaGeradas();
		ExcluiGiaGeradas();

		util.log.info("[" + util.getMetod() + "] - Clica no Botao EFD");
		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_RS\\Botao_EFD" });
		if (waitResult.region != null) {
			waitResult.region.click();
		} else {
			return false;
		}

		util.log.info("[" + util.getMetod() + "] - Clica no Botao para adicionar o endereco do arquivo");
		waitResult = utilAut.WaitFor(30, new String[] { "GIA_RS\\Botao_Open_Folder", "GIA_RS\\Botao_Open_Folder2" });
		if (waitResult.region != null) {
			waitResult.region.click();
		}

		util.log.info("[" + util.getMetod() + "] - Adiciona endereco do aquivo");
		waitResult = utilAut.WaitFor(20,
				new String[] { "GIA_RS\\Insere_endereco_arquivo", "GIA_RS\\Insere_endereco_arquivo2" });
		if (waitResult.region != null) {

			waitResult.region.click();
			s.type(enderecoDoArquivo);

			waitResult = utilAut.WaitFor(20, new String[] { "GIA_RS\\Botao_Abrir", "GIA_RS\\Botao_Open" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(2);
			}

			waitResult = utilAut.WaitFor(20, new String[] { "GIA_RS\\Botao_Importar" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

		} else {
			return false;
		}

		// Tratamento para ROT - ST

//		util.log.info("[" + util.getMetod() + "] - ROT - ST");
//		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\ROT_ST_Opcao" });
//		if (waitResult.region != null) {
//			waitResult.region.click();
		// if (!PreencherUsuarioESenha2(giaUsuario, giaSenha))
		// System.out.println("Preenchimento temporario do Usuario e senha(18/03/20)");

		util.log.info("[" + util.getMetod() + "] - ROT - ST");
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\ROT_ST_Opcao2" });
		if (waitResult.region != null) {
			waitResult.region.click();

			waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\ROT_ST_Continuar" });
			if (waitResult.region != null) {

				waitResult.region.click();

				if (!PreencherUsuarioESenha(giaUsuario, giaSenha))
					return false;

			} else {
				return false;
			}
		} else {
			if (!PreencherUsuarioESenha2(giaUsuario, giaSenha))
				util.log.info("[" + util.getMetod() + "] - Preenchimento temporario do Usuario e senha(18/03/20)");
		}

		// ****

		util.log.info("[" + util.getMetod() + "] - Aguarda import do arquivo");
		waitResult = utilAut.WaitFor(tempoMaximoImportacao,
				new String[] { "GIA_RS\\Validado_Sucesso", "GIA_RS\\Botao_Visualizar_Advertencia" });
		if (waitResult.region != null) {
			if (GravarAdvertencias()) {
				waitResult = utilAut.WaitFor(30,
						new String[] { "GIA_RS\\Botao_Fechar", "GIA_RS\\Fechar_Tela_Importar" });
				if (waitResult.region != null) {
					waitResult.region.click();
					util.sleep(2);
				}
			}
		}

		waitResult = utilAut.WaitFor(30, new String[] { "GIA_RS\\Botao_Fechar", "GIA_RS\\Fechar_Tela_Importar" });
		if (waitResult.region != null) {
			waitResult.region.click();
		}

		FechaNotificacaoDeVersoes();

		util.log.info("[" + util.getMetod() + "] - Valida se a Guia foi gerada.");
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\ValidaErrosNaCriacaoDaGIA" });
		if (waitResult.region == null)
			return false;

		return true;
	}

//-------------------------------------------------------------------------------------------------------------	
	public boolean Validar_Divergencias(String enderecoDoArquivo, String giaUsuario, String giaSenha) {

		util.log.info("[" + util.getMetod() + "] - Clica no Botao EFD");
		WaitResult waitResult = utilAut.WaitFor(30, new String[] { "GIA_RS\\Botao_GIA" });
		if (waitResult.region != null) {
			waitResult.region.click();
		} else {
			return false;
		}

		util.log.info("[" + util.getMetod() + "] - Clica na linha da GIA gerada");
		waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Faixa_Azul_GIA_Gerada" });
		if (waitResult.region != null) {
			// waitResult.region.doubleClick(); // RODOLFO -- 2020-03-04 -- falha ao abrir
			// gia
			waitResult.region.click(); // RODOLFO
			s.type(Key.ENTER); // RODOLFO
		} else {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Selecionando_GIA_Processadas]");
			util.PrintErro(watchPath + "\\Erro[Selecionando_GIAs_Processadas].png");
			return false;
		}

		ValidaSeOArquivoEhRetificacao(enderecoDoArquivo);

		util.log.info("[" + util.getMetod() + "] - Clica em validar");
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Botao_Validar" });
		if (waitResult.region != null) {
			waitResult.region.click();
		} else {
			util.log.error("[" + util.getMetod() + "] - Processamento com erro: [Botao Validar nao encontrado]");
			util.PrintErro(watchPath + "\\Erro[TelaGuiaApuracaoICMSNaoCarregada].png");
			return false;
		}

		if (!PreencherUsuarioESenha(giaUsuario, giaSenha))
			return false;

		util.log.info("[" + util.getMetod() + "] - Clica no botao Fechar na Tela de Apuracao");
		waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Tela_de_Apuracao_Botao_Fechar" });
		if (waitResult.region != null) {
			if (!GravarResultadoApuracao())
				return false;
			validaMSGErro_Access_Violation();
			waitResult.region.click();
		} else {
			return false;
		}

		return true;
	}

//--------------------------------------------------------------------------------------	
	public boolean GeraArquivosParaTransmissao(String enderecoDoArquivo, String giaUsuario, String giaSenha) {

		util.log.info("[" + util.getMetod() + "] - Ativa tela de home");
		WaitResult waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
		if (waitResult.region != null) {
			waitResult.region.click();

			util.log.info("[" + util.getMetod() + "] - Clica em Gerar arquivo para a transmissao");
			s.type("A", Key.ALT);
			util.sleep(1);
			s.type(Key.UP);
			util.sleep(1);
			s.type(Key.UP);
			util.sleep(1);
			s.type(Key.ENTER);
			util.sleep(1);

		} else {
			return false;
		}

		util.log.info("[" + util.getMetod() + "] - Clica em Gerar na tela gerar arquivo para transmissao");
		waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Valida_Tela_Gerar_Arquivo_Para_Transmissao" });
		if (waitResult.region != null) {

			util.log.info("[" + util.getMetod() + "] - Clica flegar GIA gerada");
			waitResult = utilAut.WaitFor(30, new String[] { "GIA_RS\\Flegar_GIA" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Clica na pasta para inserir o endereco");
			waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Botao_Open_Folder", "GIA_RS\\Botao_Open_Folder2" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Clica na campo para inserir o endereco");
			waitResult = utilAut.WaitFor(5,
					new String[] { "GIA_RS\\Insere_Endereco_Pasta", "GIA_RS\\Insere_Endereco_Pasta2",
							"GIA_RS\\Insert_Adrress_Folder", "GIA_RS\\Insert_Adrress_Folder2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				s.type(enderecoDoArquivo);
			} else {
				return false;
			}

			waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Selecionar_Pasta", "GIA_RS\\Select_Folder" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

			waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Botao_Gerar" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

			if (!PreencherUsuarioESenha(giaUsuario, giaSenha))
				return false;

			// waitResult = utilAut.WaitFor(10, new String[] {
			// "GIA_RS\\Botao_Transmitir_Nao" });
			waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Botao_Transmitir_Sim" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

		} else {
			return false;
		}

		return true;
	}

//--------------------------------------------------------------------------------------
	public boolean PreencherUsuarioESenha2(String user, String pass) {
		try {

			util.log.info("[" + util.getMetod() + "] - Insere o CNPJ");
			WaitResult waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Inserir_CPF", "GIA_RS\\Inserir_CPF2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(Key.BACKSPACE);
				s.type(user);
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Insere a Senha");
			waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Inserir_Senha", "GIA_RS\\Inserir_Senha2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(pass);
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Clica no botao validar divergencias");
			waitResult = utilAut.WaitFor(10,
					new String[] { "GIA_RS\\Botao_Validar_Divergencias", "GIA_RS\\Botao_VerificarOpcaoROTST" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(10);
			} else {
				return false;
			}
		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

//--------------------------------------------------------------------------------------	
	public boolean PreencherUsuarioESenha(String user, String pass) {
		try {

			util.log.info("[" + util.getMetod() + "] - Insere o CNPJ");
			WaitResult waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Inserir_CPF", "GIA_RS\\Inserir_CPF2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(Key.BACKSPACE);
				s.type(user);
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Insere a Senha");
			waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Inserir_Senha", "GIA_RS\\Inserir_Senha2" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(1);
				s.type(pass);
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Clica no botao validar divergencias");
			waitResult = utilAut.WaitFor(10,
					new String[] { "GIA_RS\\Botao_Validar_Divergencias", "GIA_RS\\Botao_VerificarOpcaoROTST" });
			if (waitResult.region != null) {
				waitResult.region.click();
				util.sleep(10);
			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Clica no botao OK na tela de atencao");// Nao_foram_encontrados_registros
			waitResult = utilAut.WaitFor(350,
					new String[] { "GIA_RS\\Botao_OK_Tela_Atencao", "GIA_RS\\Sem_Divergencias",
							"GIA_RS\\Nao_foram_encontrados_registros", "GIA_RS\\Erro_503",
							"GIA_RS\\Erro_CPF_SemVinculo", "GIA_RS\\Erro_EmpresaNaoOptanteROTST",
							"GIA_RS\\Guia_Apuracao_ICMS" });
			if (waitResult.region != null) {
				waitResult = utilAut.WaitFor(1,
						new String[] { "GIA_RS\\Sem_Divergencias", "GIA_RS\\Nao_foram_encontrados_registros" });
				if (waitResult.region != null) {
					util.log.info("[" + util.getMetod() + "] - Clica no Fechar [Sem_Divergencias] ");
					waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Sem_Divergencias_Botao_Fechar" });
					if (waitResult.region != null) {
						waitResult.region.click();

						util.log.info("[" + util.getMetod() + "] - Clica no botao OK na tela de atencao");
						waitResult = utilAut.WaitFor(10, new String[] { "GIA_RS\\Imagem_Guia_Valida" });
						if (waitResult.region != null) {
							waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Guia_Valida_Botao_OK" });
							if (waitResult.region != null) {
								waitResult.region.click();
								return true;
							}
						}

					}
				} else {
					waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Botao_OK_Tela_Atencao" });
					if (waitResult.region != null)
						waitResult.region.click();
				}

				waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Erro_503" });
				if (waitResult.region != null) {
					waitResult.region.click();
					return false;
				}

				waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Erro_CPF_SemVinculo" });
				if (waitResult.region != null) {
					waitResult.region.click();
					return false;
				}

				waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Erro_EmpresaNaoOptanteROTST" });
				if (waitResult.region != null) {
					waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Botao_ROT_ST_Fechar" });
					if (waitResult.region != null) {
						waitResult.region.click();
						// return false;
					}
					// return false;
				}

			} else {
				return false;
			}

			util.log.info("[" + util.getMetod() + "] - Clica no botao OK na tela de atencao");
			waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Imagem_Guia_Valida" });
			if (waitResult.region != null) {
				waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Guia_Valida_Botao_OK" });
				if (waitResult.region != null) {
					waitResult.region.click();
				}
			}

			util.log.info("[" + util.getMetod() + "] - Clica no botao OK na tela de atencao");
			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Fecha_MSG_de_Alerta" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}

			waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Erro_Inserindo_UsuarioSenha" });
			if (waitResult.region != null) {
				util.log.info(
						"[" + util.getMetod() + "] - Erro no carregamento da tela de Verificacao de Divergencias");
				waitResult.region.click();
				return false;
			}

		} catch (Exception e) {
			util.log.error("[" + util.getMetod() + "] - " + e.getMessage());
			e.printStackTrace();
		}

		return true;
	}

//---------------------------------------------------------------------------------------------------------------------------
	public boolean ExcluiGiaGeradas() {

		util.log.info("[" + util.getMetod() + "] - Valida se existe alguma gia criada");
		WaitResult waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Nenhuma_GIA_Criada" });
		if (waitResult.region != null)
			return true;

		util.log.info("[" + util.getMetod() + "] - Clica na linha da GIA gerada");
		waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Faixa_Azul_GIA_Gerada" });
		if (waitResult.region != null) {
			waitResult.region.click();
		} else {
			return false;
		}

		waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Clica_no_x_para_escluir_GIA" });
		if (waitResult.region != null) {
			waitResult.region.click();
		} else {
			return false;
		}

		waitResult = utilAut.WaitFor(5,
				new String[] { "GIA_RS\\Clica_em_sim_para_excluir_GIA", "GIA_RS\\Clica_em_yes_para_excluir_GIA" });
		if (waitResult.region != null) {
			waitResult.region.click();
		} else {
			return false;
		}

		return true;
	}

//----------------------------------------------------------------------------------------
	public String getTedFileName(String folderString) {
		String retorno = "";
		final File folder = new File(folderString);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.getName().endsWith(".TED")) {
				util.log.info(
						"[" + util.getMetod() + "] - Preparando arquivo " + fileEntry.getName() + " para Transmissao");
				retorno = fileEntry.getName();
			}
		}
		return retorno;
	}

//--------------------------------------------------------------------------------------------	
	public boolean Close() {

		WaitResult waitResult = utilAut.WaitFor(1, new String[] { "GIA_RS\\Titulo_Home", "GIA_RS\\Titulo_Home2" });
		if (waitResult.region != null) {
			waitResult.region.click();
			util.sleep(1);

			util.log.info("[" + util.getMetod() + "] - Clica em Fechar");
			s.type("A", Key.ALT);
			util.sleep(1);
			s.type(Key.UP);
			util.sleep(1);
			s.type(Key.ENTER);
			util.sleep(5);
		}

		return true;
	}

//-----------------------------------------------------------------------------------------------------------------------------
	public boolean GravarAdvertencias() {
		util.log.info("[" + util.getMetod() + "] - Clinca no botao visualizar");
		WaitResult waitResult = utilAut.WaitFor(20, new String[] { "GIA_RS\\Botao_Visualizar_Advertencia" });
		if (waitResult.region != null) {
			waitResult.region.click();

			if (!Gravar_PDF("Problemas_na_Importacao_do_EFD"))
				return false;

			validaMSGErro_Access_Violation();

		} else {
			return false;
		}
		return true;
	}

//----------------------------------------------------------------------------------------------	
	public boolean GravarResultadoApuracao() {
		util.log.info("[" + util.getMetod() + "] - Clinca no botao visualizar");
		WaitResult waitResult = utilAut.WaitFor(20, new String[] { "GIA_RS\\Botao_Imprimir_Resumo_das_Operacoes" });
		if (waitResult.region != null) {
			waitResult.region.click();

			waitResult = utilAut.WaitFor(20, new String[] { "GIA_RS\\Botao_Visualizar_Impressoes" });
			if (waitResult.region != null) {
				waitResult.region.click();
			} else {
				return false;
			}

			if (!Gravar_PDF("Guia_de_Informacao_e_Apuracao_do_ICMS"))
				return false;

		} else {
			return false;
		}
		return true;
	}

//---------------------------------------------------------------------------------------------------------------------------------	
	public boolean Gravar_PDF(String nomeArquivo) {
		WaitResult waitResult = utilAut.WaitFor(20, new String[] { "GIA_RS\\Botao_Salvar_Advertencia_em_pdf" });
		if (waitResult.region != null) {
			util.sleep(1);
			waitResult.region.click();

			waitResult = utilAut.WaitFor(20,
					new String[] { "GIA_RS\\Botao_Salvar_Advertencia", "GIA_RS\\Botao_Salvar_Advertencia2" });
			if (waitResult.region != null) {
				s.type(Key.DELETE);
				util.sleep(1);
				s.type(nomeArquivo);
				util.sleep(1);
				waitResult.region.click();
				util.sleep(3);
			} else {
				return false;
			}
		}

		waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Botao_Fechar_Advertencia" });
		if (waitResult.region != null) {
			waitResult.region.click();
		}

		return true;

	}

//---------------------------------------------------------------------------------------------------------------------------------	
	public void validaMSGErro_Access_Violation() {
		WaitResult waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Botao_OK_Relatorio_Exportado" });
		if (waitResult.region != null) {
			waitResult.region.click();
		}

		waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Valida_Msg_Erro_Access_Violation" });
		if (waitResult.region != null) {
			waitResult = utilAut.WaitFor(5, new String[] { "GIA_RS\\Botao_OK_Msg_Erro_Access_Violation" });
			if (waitResult.region != null)
				waitResult.region.click();
		}
	}

//---------------------------------------------------------------------------------------------------------------------------------	
	public void ValidaSeOArquivoEhRetificacao(String addressFile) {
		ArquivoFiscal arquivoFiscal = new ArquivoFiscal();
		arquivoFiscal.ArquivoRetificacao(addressFile);

		if (arquivoFiscal.getRetificadora().equalsIgnoreCase("1")) {
			WaitResult waitResult = utilAut.WaitFor(11,
					new String[] { "GIA_RS\\Clica_em_retificadora", "GIA_RS\\Clica_em_retificadora2" });
			if (waitResult.region != null) {
				waitResult.region.click();
			}
		}
	}
}
