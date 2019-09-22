package domaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBFudbalskiSavez {

	String connectionString;
	Connection con;

	public DBFudbalskiSavez(String connectionString) {
		super();
		this.connectionString = connectionString;
	}

	public void connect() {
		try {
			con = DriverManager.getConnection(connectionString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void disconnect() {
		try {
			if (con != null && !con.isClosed()) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ispisFudbaleraITimova() {
		try {
			PreparedStatement ps = con.prepareStatement(
					"select Fudbaler.Ime, Tim.Naziv from Fudbaler, Tim where Tim.IdTim = Fudbaler.IdTim");
			ResultSet rs = ps.executeQuery();
			System.out.println("Svi fudbaleri iz baze i timovi za koje igraju:");
			while (rs.next()) {
				String fudbaler = rs.getString(1);
				String tim = rs.getString(2);
				System.out.println(fudbaler + " - " + tim);
			}
			ps.close();
			rs.close();
			System.out.println();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ispisFudbaleraMU() {
		try {
			PreparedStatement ps = con.prepareStatement(
					"select Fudbaler.Ime from Fudbaler where Fudbaler.IdTim = ( select IdTim from Tim where Naziv = \"Manchester United\" )");
			ResultSet rs = ps.executeQuery();
			System.out.println("Fudbaleri koji igraju u Manchester United-u:");
			while (rs.next()) {
				String fudbaler = rs.getString(1);
				System.out.println(fudbaler);
			}
			ps.close();
			rs.close();
			System.out.println();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void brojGolova() {
		try {
			PreparedStatement ps = con.prepareStatement(
					"select Fudbaler.Ime, count (*) from Fudbaler join Gol using (IdFud) group by Fudbaler.IdFud");
			ResultSet rs = ps.executeQuery();
			System.out.println("Broj golova po igracu:");
			while (rs.next()) {
				String ime = rs.getString(1);
				int brGolova = rs.getInt(2);
				System.out.println(ime + " - " + brGolova);
			}
			ps.close();
			rs.close();
			System.out.println();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void maxBrGolova() { // ispravi. nije samo jedan sa 2gola
		try {
			PreparedStatement ps = con.prepareStatement(
					"select Fudbaler.Ime, count(*) from Fudbaler join Gol using (IdFud) group by Fudbaler.IdFud order by 2 desc limit 1");
			ResultSet rs = ps.executeQuery();
			System.out.println("Najvise postignutih golova: " + rs.getString(1) + " - " + rs.getInt(2));
			System.out.println();
			ps.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void ispisGradova(int id) {
		try {
			PreparedStatement ps2 = con.prepareStatement("select Fudbaler.Ime from Fudbaler where Fudbaler.IdFud = ?");
			ps2.setInt(1, id);
			ResultSet rs2 = ps2.executeQuery();
			System.out.println("Gradovi u kojima je igrao " + rs2.getString(1) + ": ");
			PreparedStatement ps = con.prepareStatement("select distinct Tim.Mesto from Tim, Fudbaler, Igrao, Utakmica "
					+ "where Fudbaler.IdFud = ? and Fudbaler.IdFud = Igrao.IdFud and Igrao.IdUta = Utakmica.IdUta and Tim.IdTim = Utakmica.IdDomacin");
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getString(1));
			}
			System.out.println();
			ps.close();
			ps2.close();
			rs.close();
			rs2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dodajFudbalera(String imeFudbalera, String imeTima) {
		try {
			PreparedStatement ps = con.prepareStatement("select max(IdFud) from Fudbaler");
			ResultSet rs = ps.executeQuery();
			int idZaNovogFudbalera = rs.getInt(1) + 1;
			PreparedStatement ps2 = con.prepareStatement("select Tim.IdTim from Tim where Tim.Naziv = ?");
			ps2.setString(1, imeTima);
			ResultSet rs2 = ps2.executeQuery();
			int idTima = rs2.getInt(1);
			PreparedStatement ps3 = con.prepareStatement("insert into Fudbaler(IdFud, Ime, IdTim) values (?, ?, ?)");
			ps3.setInt(1, idZaNovogFudbalera);
			ps3.setString(2, imeFudbalera);
			ps3.setInt(3, idTima);
			ps3.execute();
			ps.close();
			ps2.close();
			ps3.close();
			rs.close();
			rs2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dodajTim(String ime, String mesto) {
		try {
			PreparedStatement ps = con.prepareStatement("select max(IdTim) from Tim");
			ResultSet rs = ps.executeQuery();
			int idZaNoviTim = rs.getInt(1) + 1;
			PreparedStatement ps2 = con.prepareStatement("insert into Tim(IdTim, Naziv, Mesto) values (?, ?, ?)");
			ps2.setInt(1, idZaNoviTim);
			ps2.setString(2, ime);
			ps2.setString(3, mesto);
			ps2.execute();
			ps.close();
			rs.close();
			ps2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void dodajUtakmicu(int idGostujuci, int idDomaci, int kolo, String ishod, int godina) {
		try {
			PreparedStatement ps = con.prepareStatement("select max(IdUta) from Utakmica");
			ResultSet rs = ps.executeQuery();
			int idZaNovuUtakmicu = rs.getInt(1) + 1;
			PreparedStatement ps2 = con.prepareStatement("insert into Utakmica(IdUta, IdDomacin, IdGost, Kolo, Ishod, Godina) values (?, ?, ?, ?, ?, ?)");
			ps2.setInt(1, idZaNovuUtakmicu);
			ps2.setInt(2, idDomaci);
			ps2.setInt(3, idGostujuci);
			ps2.setInt(4, kolo);
			ps2.setString(5, ishod);
			ps2.setInt(6, godina);
			ps2.execute();
			ps.close();
			rs.close();
			ps2.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
