import SwiftUI

struct PollCreatorView: View {
    @Binding var options: [String]
    @Binding var duration: Int // Duration in days

    let maxOptions = 4
    let durations = [1, 3, 7]

    var body: some View {
        VStack(spacing: 16) {
            HStack {
                Text("Poll")
                    .font(.headline)
                Spacer()
            }

            VStack(spacing: 12) {
                ForEach(0..<options.count, id: \.self) { index in
                    HStack {
                        TextField("Option \(index + 1)", text: $options[index])
                            .textFieldStyle(RoundedBorderTextFieldStyle())

                        if options.count > 2 {
                            Button(action: {
                                options.remove(at: index)
                            }) {
                                Image(systemName: "xmark.circle.fill")
                                    .foregroundColor(.gray)
                            }
                            .accessibilityLabel("Remove option \(index + 1)")
                        }
                    }
                }
            }

            if options.count < maxOptions {
                Button(action: {
                    options.append("")
                }) {
                    HStack {
                        Image(systemName: "plus.circle.fill")
                        Text("Add Option")
                    }
                    .foregroundColor(.blue)
                }
                .frame(maxWidth: .infinity, alignment: .leading)
            }

            Divider()

            HStack {
                Text("Poll Duration")
                    .foregroundColor(.gray)
                Spacer()

                Picker("Duration", selection: $duration) {
                    ForEach(durations, id: \.self) { day in
                        Text("\(day) day\(day > 1 ? "s" : "")").tag(day)
                    }
                }
                .pickerStyle(MenuPickerStyle())
            }
        }
        .padding()
        .background(Color(.secondarySystemBackground))
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.gray.opacity(0.3), lineWidth: 1)
        )
    }
}

struct PollCreatorView_Previews: PreviewProvider {
    static var previews: some View {
        PollCreatorView(options: .constant(["Option 1", "Option 2"]), duration: .constant(1))
            .padding()
    }
}
