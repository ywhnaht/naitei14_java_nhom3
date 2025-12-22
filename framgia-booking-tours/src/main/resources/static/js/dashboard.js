        document.addEventListener("DOMContentLoaded", function () {
            let revenueChartInstance = null;
            let statusChartInstance = null;
            
            // Format tiền tệ VNĐ cho JS
            const currencyFormatter = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            });

            // Hàm vẽ biểu đồ Doanh thu (Bar Chart)
            function renderRevenueChart(data) {
                const ctx = document.getElementById('revenueChart').getContext('2d');
                
                // Chuẩn bị mảng labels (Tháng 1 -> 12) và data
                const labels = data.map(item => 'Tháng ' + item.month);
                const values = data.map(item => item.revenue);

                if (revenueChartInstance) revenueChartInstance.destroy(); // Xóa biểu đồ cũ nếu có

                revenueChartInstance = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Doanh thu',
                            data: values,
                            backgroundColor: 'rgba(59, 130, 246, 0.8)', // Blue-500
                            borderRadius: 6,
                            barThickness: 20,
                            hoverBackgroundColor: '#1d4ed8'
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return currencyFormatter.format(context.raw);
                                    }
                                }
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                grid: { borderDash: [2, 4], color: '#f3f4f6' },
                                ticks: {
                                    callback: function(value) {
                                        return value >= 1000000 ? (value / 1000000) + 'M' : value;
                                    }
                                }
                            },
                            x: {
                                grid: { display: false }
                            }
                        }
                    }
                });
            }

            // Hàm vẽ biểu đồ Trạng thái (Doughnut Chart)
            function renderStatusChart(data) {
                const ctx = document.getElementById('statusChart').getContext('2d');
                
                const labels = data.map(item => item.status);
                const values = data.map(item => item.count);
                
                // Màu sắc tương ứng cho từng trạng thái
                const colors = {
                    'PAID': '#10b981',      // Green
                    'PENDING': '#f59e0b',   // Amber
                    'CANCELLED': '#ef4444', // Red
                    'CONFIRMED': '#3b82f6', // Blue
                    'REFUNDED': '#6366f1',  // Indigo
                    'COMPLETED': '#059669'  // Emerald
                };
                
                const bgColors = labels.map(status => colors[status] || '#9ca3af');

                if (statusChartInstance) statusChartInstance.destroy();

                statusChartInstance = new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: labels,
                        datasets: [{
                            data: values,
                            backgroundColor: bgColors,
                            borderWidth: 0,
                            hoverOffset: 4
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        cutout: '70%',
                        plugins: {
                            legend: {
                                position: 'bottom',
                                labels: { usePointStyle: true, padding: 20 }
                            }
                        }
                    }
                });
            }

            // Hàm gọi API lấy dữ liệu (AJAX)
            async function fetchData(year) {
                try {
                    const response = await fetch(`/admin/dashboard/api/chart-data?year=${year}`);
                    if (!response.ok) throw new Error('Network error');
                    
                    const data = await response.json();
                    
                    // Vẽ lại 2 biểu đồ
                    renderRevenueChart(data.revenueChart);
                    renderStatusChart(data.statusChart);
                    
                } catch (error) {
                    console.error("Lỗi tải dữ liệu biểu đồ:", error);
                }
            }

            // --- INIT ---
            // 1. Lấy năm mặc định từ dropdown
            const yearFilter = document.getElementById('yearFilter');
            
            // 2. Load dữ liệu lần đầu
            fetchData(yearFilter.value);

            // 3. Bắt sự kiện khi đổi năm (Task #95213)
            yearFilter.addEventListener('change', function() {
                fetchData(this.value);
            });
        });