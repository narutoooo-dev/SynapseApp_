import SwiftUI

struct NotificationsView: View {
    @EnvironmentObject var navigator: AppNavigator
    @StateObject private var viewModel = DependencyContainer.shared.makeNotificationsViewModel()
    @State private var hasInitialFetch = false

    var body: some View {
        NavigationStack(path: $navigator.notificationsPath) {
            ZStack {
                if viewModel.isLoading && viewModel.notifications.isEmpty {
                    ProgressView("Loading notifications...")
                } else if let errorMessage = viewModel.errorMessage, viewModel.notifications.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "exclamationmark.triangle")
                            .font(.largeTitle)
                            .foregroundColor(.red)
                        Text("Error")
                            .font(.headline)
                        Text(errorMessage)
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                        Button("Retry") {
                            Task {
                                await viewModel.loadNotifications()
                            }
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    .padding()
                } else if viewModel.notifications.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "bell.slash")
                            .font(.largeTitle)
                            .foregroundColor(.secondary)
                        Text("No Notifications")
                            .font(.headline)
                        Text("You're all caught up! New notifications will appear here.")
                            .foregroundColor(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding()
                } else {
                    List {
                        ForEach(viewModel.notifications) { notification in
                            NotificationRowView(notification: notification)
                                .listRowInsets(EdgeInsets())
                                .listRowSeparator(.hidden)
                                .contentShape(Rectangle())
                                .onTapGesture {
                                    if !notification.isRead {
                                        viewModel.markAsRead(notification.id)
                                    }
                                    // Optionally handle navigation based on notification.targetId here
                                }
                        }
                    }
                    .listStyle(.plain)
                    .refreshable {
                        await viewModel.loadNotifications()
                    }
                }
            }
            .navigationTitle("Notifications")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    if !viewModel.notifications.isEmpty {
                        Button("Mark all as read") {
                            viewModel.markAllAsRead()
                        }
                        .font(.subheadline)
                    }
                }
            }
            .onAppear {
                if !hasInitialFetch {
                    hasInitialFetch = true
                    Task {
                        await viewModel.loadNotifications()
                    }
                }
            }
        }
    }
}

struct NotificationsView_Previews: PreviewProvider {
    static var previews: some View {
        NotificationsView()
            .environmentObject(AppNavigator())
    }
}
