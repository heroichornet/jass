package ch.game.jass.impartial;

import ch.game.jass.Jass;
import ch.game.jass.JassCard;
import ch.game.jass.JassDeck;
import ch.game.jass.JassTable;
import ch.game.jass.player.JassMove;
import ch.game.jass.player.JassPlayer;

import java.util.logging.Logger;


public class JassGameModerator{
	
	private static final Logger LOGGER = Logger.getLogger("Logger");
	
	private static JassPlayer nextPlayer;
	
	private static JassMove nextMove=new JassMove();
	
	private static final JassCard startingCard=
			new JassCard(JassCard.Suit.EICHEL.ordinal()
					,JassCard.Rank.BANNER.ordinal());
    
    private static int gameMode;

	public static void dealHands(JassTable table) {
		
		JassDeck deck= new JassDeck();
		deck.shuffle();
		
		int i=0;
		
		while(deck.isNotEmpty()){
			for(JassPlayer player:table.getPlayers()){
				i++;
				JassCard card=deck.removeTopCard();
				player.dealCard(card);
				LOGGER.info("dealing card "+i+" "+card);
				if(card.equals(startingCard)){
					nextPlayer=player;
				}
			}
		}
		
		LOGGER.info("Starting player ="+nextPlayer);
		
	}

	public static void letPlayerChooseGameMode(){
		System.out.println(nextPlayer+" chooses mode "+gameMode);
		gameMode=nextPlayer.chooseGameMode();
	}

	public static void setGameMode(Jass.GameMode mode) {
		gameMode = mode.ordinal();
	}

	public static void moderateRound(JassTable table) {
		
		LOGGER.info("Game Mode = "+getGameMode());
		
		for(JassPlayer player:table.getPlayers(nextPlayer)){
			
			JassCard card=player.playCard(table.getTrick());
			
			nextMove.setCard(card);
			nextMove.setPlayer(player);
			nextMove.setTrick(table.getTrick());
			
			if(JassUmpire.abidesByTheRules(nextMove)){
				table.playCardToTrick(card);
				System.out.println(player+" played "+card+" to trick.");
			}else{
				System.err.println("Player "+player+"played"+
						"illegal card to trick ");
				System.exit(0);
			}
		}
		
		nextPlayer=JassUmpire.determineWinner(table);
		JassScoreKeeper.addToPile(nextPlayer,table.getTrick());
		
		table.resetTrick();
		
	}
	
	public static JassPlayer getCurrentStartPlayer(){
		return nextPlayer;
	}
	
    public static int getGameMode() {
		return gameMode;
	}
    
    public static boolean isTrumpfGame(){
    	return (gameMode!=Jass.GameMode.OBEN.ordinal()) && 
    			(gameMode !=Jass.GameMode.UNTEN.ordinal());
    }

	public static void setGameMode(int newGameMode) {
		gameMode = newGameMode;
	}





	
}
