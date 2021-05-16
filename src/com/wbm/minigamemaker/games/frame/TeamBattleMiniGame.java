package com.wbm.minigamemaker.games.frame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public abstract class TeamBattleMiniGame extends MiniGame {

	/*
	 * [팀 배틀]
	 * 
	 * - team끼리 배틀하는 미니게임 프레임
	 * 
	 * - 점수도 팀끼리 관리
	 * 
	 * - 팀 개수, 팀 사이즈 설정 기능
	 * 
	 * - 팀 그룹 채팅 기능: 구현 클래스의 processEvent()에서 super.proprocessEvent()는 선택사항
	 * 
	 * [필수]
	 * 
	 * - initGameSetting() 메소드 super.initGameSetting() 필수!!!!!
	 * 
	 * - 팀 멤버 등록(autoTeamSetup이면 필요x): registerAllPlayersToTeam()에서 getPlayers()를
	 * 가지고 멤버 등록을 해줘야 함
	 * 
	 * - runTaskAfterStart() 메소드 super.runTaskAfterStart() 필수!
	 * 
	 */

	private List<Team> allTeams;
	// 팀 개수
	private int teamCount;
	// 팀 인원 수
	private int teamSize;
	private boolean groupChat;
	// 팀 개수, 인원수에 맞게 자동 팀 분배
	private boolean autoTeamSetup;

	// 팀 등록 강제
	protected abstract void registerAllPlayersToTeam();

	public TeamBattleMiniGame(String title, int maxPlayerCount, int timeLimit, int waitingTime, int teamCount,
			int teamSize) {
		super(title, maxPlayerCount, timeLimit, waitingTime);

		// set teamCount, teamSize
		this.fixTeamCount(teamCount);
		this.fixTeamSize(teamSize);
		// setup team
		this.setupAllTeams();
	}

	protected void setGroupChat(boolean groupChat) {
		this.groupChat = groupChat;
	}

	protected void setAutoTeamSetup(boolean autoTeamSetup) {
		//
		this.autoTeamSetup = autoTeamSetup;
	}

	private void setupAllTeams() {
		// allTeams 초기화
		if (this.allTeams == null) {
			this.allTeams = new ArrayList<Team>();
		} else {
			this.allTeams.clear();
		}

		// teamCount만큼 팀 추가
		for (int i = 0; i < this.teamCount; i++) {
			this.allTeams.add(new Team());
		}
	}

	protected void fixTeamCount(int teamCount) {
		// 팀 최대 갯수 설정
		this.teamCount = teamCount;
	}

	protected void fixTeamSize(int teamSize) {
		// 1팀당 최대 인원 설정
		this.teamSize = teamSize;
	}

	protected Team getTeam(int teamNumber) {
		return allTeams.get(teamNumber);
	}

	protected Team getPlayerTeam(Player p) {
		for (Team team : allTeams) {
			if (team.hasMember(p)) {
				return team;
			}
		}
		return null;
	}

	protected boolean isSameTeam(Player p1, Player p2) {
		/*
		 * 2 player가 같은 팀인지
		 */
		return this.getPlayerTeam(p1) == this.getPlayerTeam(p2);
	}

	protected boolean registerPlayerToTeam(Player p) {
		/*
		 * 순서대로 팀에 플레이어 추가
		 */
		for (int teamNumber = 0; teamNumber < teamCount; teamNumber++) {
			if (this.registerPlayerToTeam(p, teamNumber)) {
				return true;
			}
		}

		return false;
	}

	protected boolean registerPlayerToTeam(Player p, int teamNumber) {
		/*
		 * teamNumber 팀에 플레이어 추가
		 */
		// teamCount 검사
		if (teamNumber >= teamCount) {
			return false;
		}
		// team 가져오기
		Team team = getTeam(teamNumber);
		return team.registerMember(p);
	}

	protected boolean unregisterPlayerFromTeam(Player p) {
		/*
		 * player를 어떤 팀에서든 unregister시킴
		 */
		for (int teamNumber = 0; teamNumber < teamCount; teamNumber++) {
			Team team = getTeam(teamNumber);
			if (team.unregisterMember(p)) {
				return true;
			}
		}
		return false;
	}

	protected int getTeamCount() {
		return this.teamCount;
	}

	protected int getTeamSize() {
		return this.teamSize;
	}

	@Override
	protected final void plusScore(Player p, int score) {
		// 개인 플레이어 점수 관리 금지: final로 선언, 대신 plusScoreToTeam() 사용
	}

	@Override
	protected final void minusScore(Player p, int score) {
		// 개인 플레이어 점수 관리 금지: final로 선언, 대신 minusScoreToTeam() 사용
	}

	protected void plusScoreToTeam(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		this.plusScoreToTeam(team, score);
	}

	protected void plusScoreToTeam(Team team, int score) {
		team.plusScoreToMembers(score);
	}

	protected void minusScoreToTeam(int teamNumber, int score) {
		Team team = this.getTeam(teamNumber);
		this.minusScoreToTeam(team, score);
	}

	protected void minusScoreToTeam(Team team, int score) {
		team.minusScoreToMembers(score);
	}

	private void plusScoreMiniGameOriginal(Player p, int score) {
		// final로 선언한 메소드 말고, 원래 MiniGame의 메소드 사용
		super.plusScore(p, score);
	}

	private void minusScoreMiniGameOriginal(Player p, int score) {
		// final로 선언한 메소드 말고, 원래 MiniGame의 메소드 사용
		super.minusScore(p, score);
	}

	@Override
	protected void initGameSetting() {
		this.setupAllTeams();
	}

	@Override
	protected void runTaskAfterStart() {
		// 자동 팀 분배 (한명씩 모든 팀에 번갈아가면서 넣기)
		if (this.autoTeamSetup) {
			int teamNumber = 0;
			for (Player p : this.getPlayers()) {
				this.registerPlayerToTeam(p, teamNumber);
				teamNumber = (teamNumber + 1) % this.teamCount;
			}
		} else {
			this.registerAllPlayersToTeam();
		}
	}

	@Override
	protected void processEvent(Event event) {
		/*
		 * 그룹채팅 기능
		 * 
		 * - 사용하려면 groupChat=true와 super.processEvent()를 사용하여야 함
		 */
		// groupChat이 true일 때
		if (this.groupChat) {
			if (event instanceof AsyncPlayerChatEvent) {
				AsyncPlayerChatEvent e = (AsyncPlayerChatEvent) event;
				// 플레이어 채팅 cancel후
				e.setCancelled(true);
				Player sender = e.getPlayer();

				// 팀 내 플레이어들에게만 채팅 전송
				Team team = this.getPlayerTeam(sender);
				// [Team: worldbiomusic] go go
				team.sendMessageToAllMembers("[Team: " + sender.getName() + "] " + e.getMessage());
			}
		}
	}

	public class Team {
		/*
		 * 변수 값에 대한 접근은 Team메소드는 public
		 * 
		 * 변수 값에 대한 설정은 TeamMiniGame의 메소드로의 접근을 강제하기 위해 Team메소드는 private
		 */
		private List<Player> members;

		Team() {
			this.members = new ArrayList<Player>();
		}

		public int size() {
			return this.members.size();
		}

		public List<Player> getMembers() {
			return this.members;
		}

		public void sendMessageToAllMembers(String msg) {
			this.members.forEach(p -> p.sendMessage(msg));
		}

		private void plusScoreToMembers(int score) {
			this.getMembers().forEach(p -> plusScoreMiniGameOriginal(p, score));
		}

		private void minusScoreToMembers(int score) {
			this.getMembers().forEach(p -> minusScoreMiniGameOriginal(p, score));
		}

		private boolean registerMember(Player p) {
			// teamSize 검사
			boolean isFull = this.size() >= teamSize;
			if (isFull) {
				return false;
			} else {
				this.members.add(p);
				return true;
			}
		}

		private boolean unregisterMember(Player p) {
			return this.members.remove(p);
		}

		private boolean hasMember(Player p) {
			return this.members.contains(p);
		}

	}
}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
