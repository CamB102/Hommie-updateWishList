package coding.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import coding.db.DBUtil;
import coding.entity.Room;
import coding.entity.WishListItem;

public class WishListService {

	// insert a list of wish-list item

	public int insertWishListItems(Map<Integer, WishListItem> wishList) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int insertedId = 0;

		try {
			conn = DBUtil.makeConnection();
			String sql = "INSERT INTO `wishlist` (student_id, room_id, added_time) VALUES (?, ?, ?)";
			ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

			for (WishListItem wishlistItem : wishList.values()) {
				ps.setInt(1, wishlistItem.getStudentId());
				ps.setInt(2, wishlistItem.getRoom().getId());
				ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
				ps.addBatch();
			}

			ps.executeBatch();
			rs = ps.getGeneratedKeys();

			if (rs.next()) {
				insertedId = rs.getInt(1);
			}

		} finally {
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

		return insertedId;
	}

	public List<Room> getWishListByStudentId(Integer studentId) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Room room = null;
		List<Room> list = new ArrayList<Room>();
		try {
//			make connection to mySQL
			conn = DBUtil.makeConnection();
			ps = conn.prepareStatement("SELECT *\n" + "FROM room\n" + "JOIN wishlist ON wishlist.room_id = room.id\n"
					+ "WHERE wishlist.student_id = ?;");
			ps.setInt(1, studentId);
			rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String title = rs.getString("title");
				String description = rs.getString("description");
				int price = rs.getInt("price");
				int countBed = rs.getInt("count_bed");
				int countBath = rs.getInt("count_bath");
				String image1Url = rs.getString("image1_url");

				room = new Room(id, title, description, price, countBed, countBath, image1Url);

				list.add(room);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

		return list;
	}

	public void deleteWishListItem(Integer studentId, Integer roomId) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
//			make connection to mySQL
			conn = DBUtil.makeConnection();
			ps = conn
					.prepareStatement("DELETE FROM wishlist\n" + "WHERE student_id = ? AND room_id = ?\n" + "LIMIT 1;");
			ps.setInt(1, studentId);
			ps.setInt(2, roomId);
			rs = ps.executeQuery();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
			System.out.println("Delete sucessfully");
		}
	}

	public int getWishListCount(Integer studentId) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int wishlistCount = 0;

		try {
			// Make connection to MySQL
			conn = DBUtil.makeConnection();
			ps = conn.prepareStatement("SELECT COUNT(*) AS wishlist_count FROM wishlist WHERE student_id = ?");
			ps.setInt(1, studentId);

			rs = ps.executeQuery();

			if (rs.next()) {

				wishlistCount = rs.getInt("wishlist_count");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (conn != null) {
				conn.close();
			}
			System.out.println("Query executed successfully");
		}
		return wishlistCount;
	}
}
