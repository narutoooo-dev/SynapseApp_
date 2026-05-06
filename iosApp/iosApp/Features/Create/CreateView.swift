import SwiftUI

struct CreateView: View {
    @EnvironmentObject var navigator: AppNavigator
    @State private var isShowingCreatePost = false

    var body: some View {
        NavigationStack(path: $navigator.createPath) {
            VStack {
                Spacer()
                Button(action: {
                    isShowingCreatePost = true
                }) {
                    Text("Create New Post")
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color.blue)
                        .cornerRadius(10)
                        .padding(.horizontal)
                }
                Spacer()
            }
            .navigationTitle("Create")
            .navigationBarTitleDisplayMode(.inline)
            .fullScreenCover(isPresented: $isShowingCreatePost) {
                CreatePostScreen(onPostSuccess: {
                    isShowingCreatePost = false
                    navigator.navigate(to: .home, on: .home)
                    navigator.selectedTab = .home
                    // Force feed refresh using notification since we don't have access to the feed ViewModel here
                    NotificationCenter.default.post(name: NSNotification.Name("RefreshFeed"), object: nil)
                })
            }
        }
    }
}

struct CreateView_Previews: PreviewProvider {
    static var previews: some View {
        CreateView()
            .environmentObject(AppNavigator())
    }
}
