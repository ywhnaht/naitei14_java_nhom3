package org.example.framgiabookingtours.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.framgiabookingtours.dto.response.AdminDashboardStatsDTO;
import org.example.framgiabookingtours.dto.response.BookingStatusDTO;
import org.example.framgiabookingtours.dto.response.MonthlyRevenueDTO;
import org.example.framgiabookingtours.enums.TourStatus;
import org.example.framgiabookingtours.repository.BookingRepository;
import org.example.framgiabookingtours.repository.TourRepository;
import org.example.framgiabookingtours.repository.UserRepository;
import org.example.framgiabookingtours.service.DashboardService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final TourRepository tourRepository;

	@Override
	public AdminDashboardStatsDTO getDashboardStats() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startOfToday = now.toLocalDate().atStartOfDay();
		LocalDateTime startOf7DaysAgo = now.minusDays(7);
		LocalDateTime startOf14DaysAgo = now.minusDays(14);

		// --- 1. TÍNH DOANH THU & TĂNG TRƯỞNG (Tháng này vs Tháng trước) ---
		// Tổng doanh thu toàn thời gian
		BigDecimal totalRevenue = bookingRepository.sumTotalRevenue();
		if (totalRevenue == null)
			totalRevenue = BigDecimal.ZERO;

		// Doanh thu tháng này
		BigDecimal currentMonthRevenue = bookingRepository.sumRevenueByMonth(now.getMonthValue(), now.getYear());
		// Doanh thu tháng trước
		LocalDateTime lastMonthDate = now.minusMonths(1);
		BigDecimal lastMonthRevenue = bookingRepository.sumRevenueByMonth(lastMonthDate.getMonthValue(),
				lastMonthDate.getYear());

		// Tính % tăng trưởng doanh thu
		double revenueGrowth = calculateGrowth(currentMonthRevenue.doubleValue(), lastMonthRevenue.doubleValue());

		// --- 2. BOOKING HÔM NAY ---
		long todayBookings = bookingRepository.countByBookingDateBetween(startOfToday, now);

		// --- 3. USER MỚI & TĂNG TRƯỞNG (7 ngày qua vs 7 ngày trước đó) ---
		long newUsers7Days = userRepository.countByCreatedAtAfter(startOf7DaysAgo);
		long previous7DaysUsers = userRepository.countByCreatedAtBetween(startOf14DaysAgo, startOf7DaysAgo);

		// Tính % tăng trưởng user
		double userGrowth = calculateGrowth((double) newUsers7Days, (double) previous7DaysUsers);

		// --- 4. TOUR ---
		long totalTours = tourRepository.count();
		long activeTours = tourRepository.countByStatus(TourStatus.AVAILABLE);

		return AdminDashboardStatsDTO.builder().totalRevenue(totalRevenue).revenueGrowth(revenueGrowth)
				.todayBookings(todayBookings).newUsers7Days(newUsers7Days).userGrowth(userGrowth).totalTours(totalTours)
				.activeTours(activeTours).build();
	}

	@Override
	public List<MonthlyRevenueDTO> getRevenueChartData(int year) {
		// 1. Lấy dữ liệu thô từ DB (chỉ chứa các tháng có doanh thu)
		List<MonthlyRevenueDTO> rawData = bookingRepository.getMonthlyRevenue(year);

		// 2. Tạo Map để tra cứu nhanh: Map<Tháng, Doanh thu>
		Map<Integer, BigDecimal> revenueMap = rawData.stream()
				.collect(Collectors.toMap(MonthlyRevenueDTO::getMonth, MonthlyRevenueDTO::getRevenue));

		// 3. Tạo danh sách đủ 12 tháng (Tháng nào thiếu thì set bằng 0)
		List<MonthlyRevenueDTO> fullYearData = new ArrayList<>();
		for (int i = 1; i <= 12; i++) {
			BigDecimal revenue = revenueMap.getOrDefault(i, BigDecimal.ZERO);
			fullYearData.add(new MonthlyRevenueDTO(i, revenue));
		}

		return fullYearData;
	}

	@Override
	public List<BookingStatusDTO> getStatusChartData() {
		return bookingRepository.getBookingStatusStats();
	}

	private double calculateGrowth(double current, double previous) {
		if (previous == 0) {
			// Nếu kỳ trước bằng 0:
			// - Nếu kỳ này > 0 -> Tăng trưởng 100% (hoặc vô cùng, nhưng để 100 hiển thị cho
			// đẹp)
			// - Nếu kỳ này = 0 -> Tăng trưởng 0%
			return current > 0 ? 100.0 : 0.0;
		}
		double growth = ((current - previous) / previous) * 100;
		// Làm tròn 1 chữ số thập phân
		return Math.round(growth * 10.0) / 10.0;
	}
}