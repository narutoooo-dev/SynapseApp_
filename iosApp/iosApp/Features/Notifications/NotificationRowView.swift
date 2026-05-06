import SwiftUI

struct NotificationRowView: View {
    let notification: NotificationItem

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            // Unread indicator dot
            Circle()
                .fill(Color.blue)
                .frame(width: 8, height: 8)
                .opacity(notification.isRead ? 0 : 1)
                .padding(.top, 16) // Align with avatar roughly

            // Avatar
            if let avatarUrlString = notification.actorAvatar, let url = URL(string: avatarUrlString) {
                AsyncImage(url: url) { phase in
                    switch phase {
                    case .empty:
                        Circle()
                            .fill(Color.gray.opacity(0.3))
                            .frame(width: 40, height: 40)
                    case .success(let image):
                        image
                            .resizable()
                            .scaledToFill()
                            .frame(width: 40, height: 40)
                            .clipShape(Circle())
                    case .failure:
                        fallbackAvatar
                    @unknown default:
                        fallbackAvatar
                    }
                }
            } else {
                fallbackAvatar
            }

            // Content
            VStack(alignment: .leading, spacing: 4) {
                HStack(spacing: 4) {
                    Text(notification.actorName)
                        .font(.subheadline)
                        .fontWeight(.bold)
                        .foregroundColor(.primary)

                    Text(notification.message)
                        .font(.subheadline)
                        .foregroundColor(.primary)
                }
                .lineLimit(2)

                HStack(spacing: 6) {
                    typeIcon
                        .foregroundColor(.secondary)
                        .font(.caption)

                    Text(notification.timestamp)
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
            }
            .padding(.top, 4)

            Spacer()
        }
        .padding(.vertical, 8)
        .padding(.trailing, 16)
        .padding(.leading, 8) // Reduced leading to account for the unread dot
        .background(notification.isRead ? Color.clear : Color.blue.opacity(0.05))
    }

    private var fallbackAvatar: some View {
        Circle()
            .fill(Color.gray.opacity(0.3))
            .frame(width: 40, height: 40)
            .overlay(
                Text(String(notification.actorName.prefix(1)).uppercased())
                    .font(.subheadline)
                    .foregroundColor(.gray)
            )
    }

    @ViewBuilder
    private var typeIcon: some View {
        switch notification.type.lowercased() {
        case "like":
            Image(systemName: "heart.fill")
                .foregroundColor(.red)
        case "comment":
            Image(systemName: "bubble.right.fill")
                .foregroundColor(.blue)
        case "follow":
            Image(systemName: "person.badge.plus.fill")
                .foregroundColor(.green)
        case "mention":
            Image(systemName: "at")
                .foregroundColor(.purple)
        default:
            Image(systemName: "bell.fill")
        }
    }
}
